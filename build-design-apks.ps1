# Gera os dois APKs de design (A e B) para instalar lado a lado no celular.
# Uso: .\build-design-apks.ps1

$ErrorActionPreference = "Stop"
$ProjectRoot = $PSScriptRoot
Set-Location $ProjectRoot

$studioJbr = "C:\Program Files\Android\Android Studio\jbr"
if (Test-Path "$studioJbr\bin\java.exe") {
    $env:JAVA_HOME = $studioJbr
} elseif (-not $env:JAVA_HOME) {
    Write-Error "JAVA_HOME nao definido. Instale o Android Studio ou configure JAVA_HOME."
}

Write-Host "Corridometro — gerando APKs de design A e B..." -ForegroundColor Cyan
Write-Host ""

& .\gradlew.bat assembleStandardConceptADebug assembleStandardConceptBDebug
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

$outDir = Join-Path $ProjectRoot "app\designs\output"
New-Item -ItemType Directory -Force -Path $outDir | Out-Null

$variants = @(
    @{
        Name = "a"
        GradlePath = "app\build\outputs\apk\standardConceptA\debug\app-standard-conceptA-debug.apk"
    },
    @{
        Name = "b"
        GradlePath = "app\build\outputs\apk\standardConceptB\debug\app-standard-conceptB-debug.apk"
    }
)

foreach ($v in $variants) {
    $src = Join-Path $ProjectRoot $v.GradlePath
    if (-not (Test-Path $src)) {
        $alt = Get-ChildItem -Path (Join-Path $ProjectRoot "app\build\outputs\apk") -Recurse -Filter "*concept$($v.Name)*debug*.apk" -ErrorAction SilentlyContinue |
            Select-Object -First 1
        if ($alt) { $src = $alt.FullName }
    }
    if (-not (Test-Path $src)) {
        Write-Error "APK conceito $($v.Name) nao encontrado apos o build. Procure em app\build\outputs\apk\"
    }
    $dest = Join-Path $outDir "corridometro-design-$($v.Name)-debug.apk"
    Copy-Item -Path $src -Destination $dest -Force
    $mb = [math]::Round((Get-Item $dest).Length / 1MB, 2)
    Write-Host "OK  $dest  ($mb MB)" -ForegroundColor Green
}

Write-Host ""
Write-Host "Instale os dois no celular:" -ForegroundColor Yellow
Write-Host "  - Corridometro A (icone verde) — com.corridometro"
Write-Host "  - Corridometro B (icone azul)   — com.corridometro.design.b"
Write-Host ""
Write-Host "Pasta: $outDir"
Write-Host ""

$open = Read-Host "Abrir pasta no Explorer? (S/n)"
if ($open -ne "n" -and $open -ne "N") {
    Start-Process -FilePath explorer.exe -ArgumentList $outDir
}
