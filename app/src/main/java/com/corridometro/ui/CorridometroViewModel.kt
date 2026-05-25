package com.corridometro.ui

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.corridometro.data.CorridometroRepository
import com.corridometro.data.billing.BillingManager
import com.corridometro.data.billing.PREMIUM_MONTHLY_ID
import com.corridometro.data.billing.PremiumPlan
import com.corridometro.ui.components.SubscriptionPlanUi
import com.corridometro.data.auth.FirebaseSetupStep
import com.corridometro.data.auth.GoogleAuthManager
import com.corridometro.data.auth.buildFirebaseSetupSteps
import com.corridometro.domain.DayHistoryItem
import com.corridometro.domain.DayUpdateMode
import com.corridometro.domain.Expense
import com.corridometro.domain.ExpenseCategoryTotal
import com.corridometro.domain.PeriodFilter
import com.corridometro.domain.Platform
import com.corridometro.domain.Summary
import com.corridometro.domain.WorkShift
import com.corridometro.domain.buildDayHistory
import com.corridometro.domain.buildPlatformBreakdown
import com.corridometro.domain.calculateSummary
import com.corridometro.domain.filterExpenses
import com.corridometro.domain.filterShifts
import com.corridometro.domain.groupExpensesByCategory
import com.corridometro.domain.mergeShiftsForDay
import com.corridometro.domain.toEpochDayLong
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DuplicateDayPrompt(
    val incoming: WorkShift,
    val existing: List<WorkShift>,
    val dayExpenses: List<Expense> = emptyList(),
)

data class CorridometroUiState(
    val workShifts: List<WorkShift> = emptyList(),
    val expenses: List<Expense> = emptyList(),
    val filteredShifts: List<WorkShift> = emptyList(),
    val filteredExpenses: List<Expense> = emptyList(),
    val summary: Summary = Summary(),
    val platformBreakdown: List<Pair<Platform, Summary>> = emptyList(),
    val expenseBreakdown: List<ExpenseCategoryTotal> = emptyList(),
    val dayHistory: List<DayHistoryItem> = emptyList(),
    val finalizedDays: Set<Long> = emptySet(),
    val finalizedAtByDay: Map<Long, Long> = emptyMap(),
    val period: PeriodFilter = PeriodFilter.MES,
    /** Vazio = total de todos os apps; não vazio = soma só dos selecionados. */
    val selectedPlatforms: Set<Platform> = emptySet(),
    val cloudConfigured: Boolean = false,
    val signedInEmail: String? = null,
    val syncMessage: String? = null,
    val isSyncing: Boolean = false,
    val duplicateDayPrompt: DuplicateDayPrompt? = null,
    val journeyMessage: String? = null,
    val addExpensesPromptDay: Long? = null,
    val scrollToExpensesSignal: Int = 0,
    val openDayReportEpochDay: Long? = null,
    val requireGoogleLogin: Boolean = false,
    val authReady: Boolean = true,
    val isAuthenticated: Boolean = true,
    val signedInDisplayName: String? = null,
    val applicationId: String = "",
    val hasGoogleServicesFile: Boolean = false,
    val firebaseSetupSteps: List<FirebaseSetupStep> = emptyList(),
    val isPremium: Boolean = false,
    val subscriptionPlans: List<SubscriptionPlanUi> = emptyList(),
    val selectedSubscriptionProductId: String = PREMIUM_MONTHLY_ID,
    val isBillingReady: Boolean = false,
    val isPurchasing: Boolean = false,
    val billingMessage: String? = null,
    val customRangeStart: Long? = null,
    val customRangeEnd: Long? = null,
    val customPeriodLabel: String? = null,
    val editingShift: WorkShift? = null,
    val navigateToJourneySignal: Int = 0,
    val openJourneyFormSignal: Int = 0,
    /** Incrementado após salvar jornada nova — UI pode exibir intersticial. */
    val journeySavedAdSignal: Int = 0,
)

class CorridometroViewModel(
    private val repository: CorridometroRepository,
    private val googleAuth: GoogleAuthManager,
    private val billingManager: BillingManager,
    private val requireGoogleLogin: Boolean,
    private val hasGoogleServicesFile: Boolean,
    private val applicationId: String,
) : ViewModel() {

    private val _authReady = MutableStateFlow(!requireGoogleLogin)
    private val _period = MutableStateFlow(PeriodFilter.MES)
    private val _customRangeStart = MutableStateFlow<Long?>(null)
    private val _customRangeEnd = MutableStateFlow<Long?>(null)
    private val _selectedPlatforms = MutableStateFlow<Set<Platform>>(emptySet())
    private val _syncMessage = MutableStateFlow<String?>(null)
    private val _isSyncing = MutableStateFlow(false)
    private val _duplicateDayPrompt = MutableStateFlow<DuplicateDayPrompt?>(null)
    private val _journeyMessage = MutableStateFlow<String?>(null)
    private val _addExpensesPromptDay = MutableStateFlow<Long?>(null)
    private val _scrollToExpensesSignal = MutableStateFlow(0)
    private val _openDayReportEpochDay = MutableStateFlow<Long?>(null)
    private val _editingShift = MutableStateFlow<WorkShift?>(null)
    private val _navigateToJourneySignal = MutableStateFlow(0)
    private val _openJourneyFormSignal = MutableStateFlow(0)
    private val _journeySavedAdSignal = MutableStateFlow(0)

    val period: StateFlow<PeriodFilter> = _period.asStateFlow()
    val selectedPlatforms: StateFlow<Set<Platform>> = _selectedPlatforms.asStateFlow()
    val openDayReportEpochDay: StateFlow<Long?> = _openDayReportEpochDay.asStateFlow()

    val signInIntent: Intent?
        get() = googleAuth.getSignInIntent()

    private val coreState = combine(
        repository.workShifts,
        repository.expenses,
        repository.finalizedDays,
        repository.finalizedAtByDay,
        _period,
        _selectedPlatforms,
        _customRangeStart,
        _customRangeEnd,
    ) { values ->
        values
    }

    private val metaState = combine(
        googleAuth.userEmail,
        googleAuth.displayName,
        googleAuth.isSignedInFlow,
        _authReady,
        _syncMessage,
        _isSyncing,
        _duplicateDayPrompt,
        _journeyMessage,
        _addExpensesPromptDay,
        _scrollToExpensesSignal,
        _openDayReportEpochDay,
        _editingShift,
        _navigateToJourneySignal,
        _openJourneyFormSignal,
        _journeySavedAdSignal,
    ) { values ->
        values
    }

    private val billingState = combine(
        billingManager.isPremium,
        billingManager.planPriceLabels,
        billingManager.selectedProductId,
        billingManager.isBillingReady,
        billingManager.billingMessage,
        billingManager.isPurchasing,
    ) { values ->
        values
    }

    init {
        if (requireGoogleLogin) {
            viewModelScope.launch {
                googleAuth.refreshAuthState()
                if (googleAuth.isSignedIn) {
                    repository.syncWithCloud()
                        .onFailure { e ->
                            _syncMessage.value = e.message ?: "Erro ao sincronizar."
                        }
                }
                _authReady.value = true
            }
        }
    }

    val uiState: StateFlow<CorridometroUiState> = combine(
        coreState,
        metaState,
        billingState,
    ) { core, meta, billing ->
        @Suppress("UNCHECKED_CAST")
        val shifts = core[0] as List<WorkShift>
        @Suppress("UNCHECKED_CAST")
        val expenses = core[1] as List<Expense>
        @Suppress("UNCHECKED_CAST")
        val finalizedDays = core[2] as Set<Long>
        @Suppress("UNCHECKED_CAST")
        val finalizedAtByDay = core[3] as Map<Long, Long>
        val period = core[4] as PeriodFilter
        val selectedPlatforms = core[5] as Set<Platform>
        val customStart = core[6] as Long?
        val customEnd = core[7] as Long?

        val email = meta[0] as String?
        val displayName = meta[1] as String?
        val signedIn = meta[2] as Boolean
        val authReady = meta[3] as Boolean
        val syncMsg = meta[4] as String?
        val syncing = meta[5] as Boolean
        val duplicatePrompt = meta[6] as DuplicateDayPrompt?
        val journeyMsg = meta[7] as String?
        val addExpensesDay = meta[8] as Long?
        val scrollSignal = meta[9] as Int
        val openReport = meta[10] as Long?
        val editingShift = meta[11] as WorkShift?
        val navigateToJourney = meta[12] as Int
        val openJourneyForm = meta[13] as Int
        val journeySavedAdSignal = meta[14] as Int
        val isAuthenticated = !requireGoogleLogin || signedIn

        val isPremium = billing[0] as Boolean
        @Suppress("UNCHECKED_CAST")
        val planPrices = billing[1] as Map<String, String>
        val selectedProductId = billing[2] as String
        val billingReady = billing[3] as Boolean
        val billingMsg = billing[4] as String?
        val purchasing = billing[5] as Boolean
        val subscriptionPlans = PremiumPlan.entries.map { plan ->
            SubscriptionPlanUi(
                plan = plan,
                priceLabel = planPrices[plan.productId] ?: plan.fallbackPrice,
            )
        }

        val periodShifts = filterShifts(shifts, period, emptySet(), customStart, customEnd)
        val periodExpenses = filterExpenses(expenses, period, emptySet(), customStart, customEnd)
        val filteredShifts = filterShifts(shifts, period, selectedPlatforms, customStart, customEnd)
        val filteredExpenses = filterExpenses(expenses, period, selectedPlatforms, customStart, customEnd)
        val summary = calculateSummary(filteredShifts, filteredExpenses)
        val breakdown = buildPlatformBreakdown(periodShifts, periodExpenses, selectedPlatforms)
        val customLabel = if (period == PeriodFilter.PERSONALIZADO && customStart != null && customEnd != null) {
            val start = com.corridometro.util.formatDate(customStart)
            val end = com.corridometro.util.formatDate(customEnd)
            if (start == end) start else "$start – $end"
        } else {
            null
        }

        CorridometroUiState(
            workShifts = shifts,
            expenses = expenses,
            filteredShifts = filteredShifts,
            filteredExpenses = filteredExpenses,
            summary = summary,
            platformBreakdown = breakdown,
            expenseBreakdown = groupExpensesByCategory(filteredExpenses),
            dayHistory = buildDayHistory(shifts, finalizedDays),
            finalizedDays = finalizedDays,
            finalizedAtByDay = finalizedAtByDay,
            period = period,
            selectedPlatforms = selectedPlatforms,
            cloudConfigured = googleAuth.isCloudConfigured,
            signedInEmail = email,
            signedInDisplayName = displayName,
            syncMessage = syncMsg,
            isSyncing = syncing,
            duplicateDayPrompt = duplicatePrompt,
            journeyMessage = journeyMsg,
            addExpensesPromptDay = addExpensesDay,
            scrollToExpensesSignal = scrollSignal,
            openDayReportEpochDay = openReport,
            requireGoogleLogin = requireGoogleLogin,
            authReady = authReady,
            isAuthenticated = isAuthenticated,
            applicationId = applicationId,
            hasGoogleServicesFile = hasGoogleServicesFile,
            firebaseSetupSteps = buildFirebaseSetupSteps(
                hasGoogleServicesFile = hasGoogleServicesFile,
                hasWebClientId = googleAuth.hasWebClientId,
                firebaseInitialized = googleAuth.firebaseInitialized,
                applicationId = applicationId,
            ),
            isPremium = isPremium,
            subscriptionPlans = subscriptionPlans,
            selectedSubscriptionProductId = selectedProductId,
            isBillingReady = billingReady,
            isPurchasing = purchasing,
            billingMessage = billingMsg,
            customRangeStart = customStart,
            customRangeEnd = customEnd,
            customPeriodLabel = customLabel,
            editingShift = editingShift,
            navigateToJourneySignal = navigateToJourney,
            openJourneyFormSignal = openJourneyForm,
            journeySavedAdSignal = journeySavedAdSignal,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CorridometroUiState(
            cloudConfigured = googleAuth.isCloudConfigured,
            requireGoogleLogin = requireGoogleLogin,
            authReady = !requireGoogleLogin,
            isAuthenticated = !requireGoogleLogin,
            applicationId = applicationId,
            hasGoogleServicesFile = hasGoogleServicesFile,
        ),
    )

    fun setPeriod(period: PeriodFilter) {
        _period.value = period
        if (period != PeriodFilter.PERSONALIZADO) {
            _customRangeStart.value = null
            _customRangeEnd.value = null
        }
    }

    fun applyCustomDateRange(start: LocalDate, end: LocalDate) {
        val startDay = start.toEpochDayLong()
        val endDay = end.toEpochDayLong()
        _customRangeStart.value = minOf(startDay, endDay)
        _customRangeEnd.value = maxOf(startDay, endDay)
        _period.value = PeriodFilter.PERSONALIZADO
    }

    fun togglePlatformFilter(platform: Platform) {
        _selectedPlatforms.value = _selectedPlatforms.value.toMutableSet().apply {
            if (platform in this) remove(platform) else add(platform)
        }
    }

    fun clearSyncMessage() = Unit.also { _syncMessage.value = null }
    fun clearJourneyMessage() = Unit.also { _journeyMessage.value = null }
    fun dismissDuplicateDayPrompt() = Unit.also { _duplicateDayPrompt.value = null }
    fun closeDayReport() = Unit.also { _openDayReportEpochDay.value = null }

    fun dismissAddExpensesPrompt() = Unit.also { _addExpensesPromptDay.value = null }

    fun confirmAddExpenses(yes: Boolean) {
        val day = _addExpensesPromptDay.value ?: return
        _addExpensesPromptDay.value = null
        if (yes) {
            _scrollToExpensesSignal.value += 1
            _journeyMessage.value = "Adicione os gastos do dia ${com.corridometro.util.formatDate(day)}."
        }
    }

    fun openDayReport(dateEpochDay: Long) {
        _openDayReportEpochDay.value = dateEpochDay
    }

    fun navigateToJourney() {
        _navigateToJourneySignal.value += 1
    }

    fun navigateToJourneyForm() {
        _openJourneyFormSignal.value += 1
    }

    fun consumeOpenJourneyFormSignal() {
        // sinal consumido na UI via LaunchedEffect
    }

    fun startEditShift(shift: WorkShift) {
        _openDayReportEpochDay.value = null
        _editingShift.value = shift
        _openJourneyFormSignal.value += 1
    }

    fun clearEditingShift() {
        _editingShift.value = null
    }

    fun updateWorkShift(shift: WorkShift, dayExpenses: List<Expense>) {
        viewModelScope.launch {
            repository.updateWorkShift(shift)
            persistDayExpenses(shift.dateEpochDay, shift.platform, dayExpenses)
            _editingShift.value = null
            _journeyMessage.value = "Jornada atualizada."
            syncIfSignedIn()
        }
    }

    fun requestSaveWorkShift(shift: WorkShift, dayExpenses: List<Expense> = emptyList()) {
        viewModelScope.launch {
            val existing = repository.getShiftsForDay(shift.dateEpochDay)
            if (existing.isEmpty()) {
                saveWorkShiftDirect(shift, dayExpenses)
                return@launch
            }
            val samePlatform = existing.filter { it.platform == shift.platform }
            if (samePlatform.isEmpty()) {
                repository.addWorkShift(shift)
                persistDayExpenses(shift.dateEpochDay, shift.platform, dayExpenses)
                onJourneySaved(shift.dateEpochDay, dayExpenses.any { it.amount > 0 })
                syncIfSignedIn()
            } else {
                _duplicateDayPrompt.value = DuplicateDayPrompt(shift, existing, dayExpenses)
            }
        }
    }

    fun confirmSaveWorkShift(mode: DayUpdateMode) {
        val prompt = _duplicateDayPrompt.value ?: return
        viewModelScope.launch {
            val incoming = prompt.incoming
            val keepOtherApps = prompt.existing.filter { it.platform != incoming.platform }
            when (mode) {
                DayUpdateMode.ADD -> {
                    repository.addWorkShift(incoming.copy(id = 0))
                }
                DayUpdateMode.SUM -> {
                    val samePlatform = prompt.existing.filter { it.platform == incoming.platform }
                    val merged = mergeShiftsForDay(samePlatform, incoming)
                    repository.replaceShiftsForDay(incoming.dateEpochDay, keepOtherApps + merged)
                }
                DayUpdateMode.REPLACE -> {
                    repository.replaceShiftsForDay(
                        incoming.dateEpochDay,
                        keepOtherApps + incoming.copy(id = 0),
                    )
                }
            }
            persistDayExpenses(incoming.dateEpochDay, incoming.platform, prompt.dayExpenses)
            _duplicateDayPrompt.value = null
            onJourneySaved(incoming.dateEpochDay, prompt.dayExpenses.any { it.amount > 0 })
            syncIfSignedIn()
        }
    }

    private suspend fun saveWorkShiftDirect(shift: WorkShift, dayExpenses: List<Expense>) {
        repository.addWorkShift(shift)
        persistDayExpenses(shift.dateEpochDay, shift.platform, dayExpenses)
        onJourneySaved(shift.dateEpochDay, dayExpenses.any { it.amount > 0 })
        syncIfSignedIn()
    }

    private suspend fun persistDayExpenses(
        dateEpochDay: Long,
        platform: Platform,
        expenses: List<Expense>,
    ) {
        repository.mergeJourneyExpensesForDay(dateEpochDay, platform, expenses)
    }

    private fun onJourneySaved(dateEpochDay: Long, hasExpenses: Boolean) {
        _journeyMessage.value = "Jornada salva."
        _journeySavedAdSignal.value += 1
        if (!hasExpenses) {
            _addExpensesPromptDay.value = dateEpochDay
        }
    }

    fun finalizeDay(dateEpochDay: Long) {
        viewModelScope.launch {
            val shifts = repository.getShiftsForDay(dateEpochDay)
            if (shifts.isEmpty()) {
                _journeyMessage.value = "Salve uma jornada antes de finalizar o dia."
                return@launch
            }
            repository.finalizeDay(dateEpochDay)
            _journeyMessage.value = "Relatório do dia salvo."
            _openDayReportEpochDay.value = dateEpochDay
            syncIfSignedIn()
        }
    }

    private suspend fun syncIfSignedIn() {
        if (googleAuth.isSignedIn) {
            repository.syncWithCloud()
        }
    }

    fun onGoogleSignInResult(data: Intent?) {
        viewModelScope.launch {
            _isSyncing.value = true
            googleAuth.handleSignInResult(data)
                .onSuccess {
                    repository.syncWithCloud()
                        .onSuccess {
                            _syncMessage.value = if (requireGoogleLogin) {
                                "Conta conectada. Seus dados foram carregados."
                            } else {
                                "Dados salvos na sua conta Google."
                            }
                        }
                        .onFailure { e ->
                            _syncMessage.value = e.message ?: "Erro ao sincronizar."
                        }
                }
                .onFailure { e ->
                    _syncMessage.value = e.message ?: "Erro ao entrar com Google."
                }
            _isSyncing.value = false
        }
    }

    fun signOut() {
        viewModelScope.launch {
            googleAuth.signOut()
            _syncMessage.value = if (requireGoogleLogin) {
                "Sessão encerrada. Entre com Google para continuar."
            } else {
                "Saiu da conta Google. Dados locais permanecem no celular."
            }
        }
    }

    fun syncNow() {
        viewModelScope.launch {
            _isSyncing.value = true
            repository.syncWithCloud()
                .onSuccess { _syncMessage.value = "Sincronizado com a conta Google." }
                .onFailure { e -> _syncMessage.value = e.message ?: "Erro ao sincronizar." }
            _isSyncing.value = false
        }
    }

    fun addExpense(expense: Expense) {
        viewModelScope.launch {
            repository.addExpense(expense)
            _journeyMessage.value = "Gasto de ${expense.category.label} adicionado."
            syncIfSignedIn()
        }
    }

    fun deleteWorkShift(shift: WorkShift) {
        viewModelScope.launch { repository.deleteWorkShift(shift) }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch { repository.deleteExpense(expense) }
    }

    fun selectSubscriptionPlan(productId: String) {
        billingManager.selectPlan(productId)
    }

    fun purchasePremium() {
        billingManager.launchPremiumPurchase()
    }

    fun restorePremium() {
        billingManager.refreshPurchases()
    }

    fun clearBillingMessage() {
        billingManager.clearBillingMessage()
    }

    class Factory(
        private val repository: CorridometroRepository,
        private val googleAuth: GoogleAuthManager,
        private val billingManager: BillingManager,
        private val requireGoogleLogin: Boolean,
        private val hasGoogleServicesFile: Boolean,
        private val applicationId: String,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CorridometroViewModel::class.java)) {
                return CorridometroViewModel(
                    repository,
                    googleAuth,
                    billingManager,
                    requireGoogleLogin,
                    hasGoogleServicesFile,
                    applicationId,
                ) as T
            }
            throw IllegalArgumentException("ViewModel desconhecido")
        }
    }
}
