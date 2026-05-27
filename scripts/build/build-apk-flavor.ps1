# Gera APK debug por edition + design.
# Uso: .\scripts\build\build-apk-flavor.ps1 standard conceptA

param(
    [Parameter(Mandatory = $true)]
    [ValidateSet("standard", "login")]
    [string]$Flavor,
    [ValidateSet("conceptA", "conceptB")]
    [string]$Design = "conceptA"
)

$ErrorActionPreference = "Stop"
. "$PSScriptRoot\..\lib\Common.ps1"
$root = Get-CorridometroRoot
Set-Location $root
Set-CorridometroJavaHome

$editionName = $Flavor.Substring(0, 1).ToUpper() + $Flavor.Substring(1)
$designName = $Design.Substring(0, 1).ToUpper() + $Design.Substring(1)
$task = "assemble$editionName$designName" + "Debug"
Write-Host "Corridometro — $Flavor/$Design — $task" -ForegroundColor Cyan
& .\gradlew.bat $task
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

$apk = Join-Path $root "app\build\outputs\apk\$Flavor\$Design\debug\app-$Flavor-$Design-debug.apk"
if (-not (Test-Path $apk)) {
    Write-Error "APK nao encontrado: $apk"
}

Write-Host "BUILD OK: $apk" -ForegroundColor Green
return $apk
