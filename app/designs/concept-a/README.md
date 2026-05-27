# Corridômetro A (mockup Início + Jornada evoluída)

## Início (mockup imagem)
- Barra com logo, herói de lucro (toque → Jornada)
- Período Hoje / 7d / 30d / Tudo
- Filtro horizontal: **12 apps** (corrida + entrega)
- 3 métricas com ripple
- Lista por aplicativo (toque filtra)
- FAB **Nova jornada**

## Jornada (Material em blocos)
1. **Registrar** — apps em 2 fileiras (corrida + delivery), data/horário, formulário expansível
2. **Prévia ao vivo** — grid 2×2 (km, online, combustível, R$/h)
3. **Hoje** — histórico + relatórios

- FAB verde **Salvar** fixo (scroll otimizado com `LazyColumn` + keys)
- Ripple e área mínima 48dp em todos os toques

## Apps suportados
**Corrida:** Uber, 99, inDrive, Cabify, Bolt, Lady Driver  
**Entrega:** iFood, Rappi, Uber Eats, Mercado, Shopee, Loggi, Amazon

Build: `.\gradlew assembleStandardConceptADebug`
