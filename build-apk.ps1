# Gera app-debug.apk com ARM (celular) + x86_64 (emulador)
# Uso: .\build-apk.ps1

$ErrorActionPreference = "Stop"
$ProjectRoot = $PSScriptRoot
Set-Location $ProjectRoot

$studioJbr = "C:\Program Files\Android\Android Studio\jbr"
if (Test-Path "$studioJbr\bin\java.exe") {
    $env:JAVA_HOME = $studioJbr
} elseif (-not $env:JAVA_HOME) {
    Write-Error "JAVA_HOME nao definido. Instale o Android Studio ou configure JAVA_HOME."
}

Write-Host "Corridometro - gerando APK debug..." -ForegroundColor Cyan
Write-Host ""

if (-not (Test-Path ".\gradlew.bat")) {
    Write-Host "Gradle Wrapper ausente. Criando..." -ForegroundColor Yellow
    $gradleDist = Get-ChildItem "$env:USERPROFILE\.gradle\wrapper\dists\gradle-8.9-bin" -Recurse -Filter "gradle.bat" -ErrorAction SilentlyContinue |
        Select-Object -First 1
    if (-not $gradleDist) {
        Write-Error "Gradle 8.9 nao encontrado. Abra o projeto no Android Studio uma vez (Sync) e tente de novo."
    }
    & $gradleDist.FullName wrapper --gradle-version 8.9
}

# App oficial = variante standard + conceito A (Inicio novo). Para os dois designs: .\build-design-apks.ps1
& .\gradlew.bat clean assembleStandardConceptADebug
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

$apk = Join-Path $ProjectRoot "app\build\outputs\apk\standardConceptA\debug\app-standard-conceptA-debug.apk"
if (-not (Test-Path $apk)) {
    $apk = Get-ChildItem -Path (Join-Path $ProjectRoot "app\build\outputs\apk") -Recurse -Filter "*conceptA*debug*.apk" -ErrorAction SilentlyContinue |
        Select-Object -First 1 -ExpandProperty FullName
}

if (-not (Test-Path $apk)) {
    Write-Error "APK nao encontrado apos o build."
}

$info = Get-Item $apk
$mb = [math]::Round($info.Length / 1MB, 2)

Write-Host ""
Write-Host "BUILD OK" -ForegroundColor Green
Write-Host "Arquivo: $($info.FullName)"
Write-Host "Tamanho: $mb MB"
Write-Host ""
Write-Host "Proximo passo: copie o APK para o Moto G31 (Downloads) e instale pelo app Arquivos."
Write-Host ""

$open = Read-Host "Abrir pasta do APK no Explorer? (S/n)"
if ($open -ne "n" -and $open -ne "N") {
    $arg = "/select," + $info.FullName
    Start-Process -FilePath explorer.exe -ArgumentList $arg
}
