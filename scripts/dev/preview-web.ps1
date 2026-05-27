# Preview web estatico (mockups HTML).
# Uso: .\scripts\dev\preview-web.ps1

$ErrorActionPreference = "Stop"
. "$PSScriptRoot\..\lib\Common.ps1"
$root = Get-CorridometroRoot
$dir = Join-Path $root "preview-web"
$port = 8765

if (-not (Test-Path (Join-Path $dir "index.html"))) {
    Write-Error "Arquivo nao encontrado: preview-web/index.html"
}

$url = "http://localhost:$port/"
Write-Host "Preview web: $url" -ForegroundColor Green
Write-Host "Ctrl+C para encerrar"
Start-Process $url
Set-Location $dir

if (Get-Command python -ErrorAction SilentlyContinue) {
    python -m http.server $port
} else {
    Start-Process (Join-Path $dir "index.html")
}
