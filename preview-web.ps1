# Servidor local para pre-visualizar a UI no navegador
# Uso: .\preview-web.ps1
# Abre: http://localhost:8765

$ErrorActionPreference = "Stop"
$root = $PSScriptRoot
$dir = Join-Path $root "preview-web"
$port = 8765

if (-not (Test-Path (Join-Path $dir "index.html"))) {
    Write-Error "Arquivo nao encontrado: preview-web/index.html"
}

$url = "http://localhost:$port/"
Write-Host "Corridometro - pre-visualizacao web" -ForegroundColor Cyan
Write-Host "  $url" -ForegroundColor Green
Write-Host "  Ctrl+C para encerrar" -ForegroundColor Yellow
Write-Host ""

Start-Process $url
Set-Location $dir

$py = Get-Command python -ErrorAction SilentlyContinue
if ($py) {
    python -m http.server $port
} else {
    Write-Host "Python nao encontrado. Abrindo arquivo direto no navegador..." -ForegroundColor Yellow
    Start-Process (Join-Path $dir "index.html")
}
