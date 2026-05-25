# Gera APK debug combinando edition + design.
# Uso: .\build-apk-flavor.ps1 standard
#      .\build-apk-flavor.ps1 standard conceptB
#      .\build-apk-flavor.ps1 login

param(
    [Parameter(Mandatory = $true)]
    [ValidateSet("standard", "login")]
    [string]$Flavor,
    [ValidateSet("conceptA", "conceptB")]
    [string]$Design = "conceptA"
)

$ErrorActionPreference = "Stop"
$ProjectRoot = $PSScriptRoot
Set-Location $ProjectRoot

$studioJbr = "C:\Program Files\Android\Android Studio\jbr"
if (Test-Path "$studioJbr\bin\java.exe") {
    $env:JAVA_HOME = $studioJbr
} elseif (-not $env:JAVA_HOME) {
    Write-Error "JAVA_HOME nao definido."
}

$editionName = $Flavor.Substring(0,1).ToUpper() + $Flavor.Substring(1)
$designName = $Design.Substring(0,1).ToUpper() + $Design.Substring(1)
$task = "assemble$editionName$designName" + "Debug"
Write-Host "Corridometro - $Flavor/$Design - $task" -ForegroundColor Cyan
& .\gradlew.bat $task
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

$apk = Join-Path $ProjectRoot "app\build\outputs\apk\$Flavor\$Design\debug\app-$Flavor-$Design-debug.apk"
if (-not (Test-Path $apk)) {
    Write-Error "APK nao encontrado: $apk"
}

Write-Host "BUILD OK: $apk" -ForegroundColor Green
return $apk
