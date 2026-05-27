# Gera mipmap/ic_launcher a partir do PNG do logo.
# Uso: .\scripts\setup\update-launcher-icon.ps1

param(
    [string]$Source = ""
)

$ErrorActionPreference = "Stop"
. "$PSScriptRoot\..\lib\Common.ps1"
$root = Get-CorridometroRoot
if (-not $Source) {
    $Source = Join-Path $root "app\designs\logo\corridometro-logo-pneu-medidor.png"
}

Add-Type -AssemblyName System.Drawing

function Save-ResizedPng {
    param([string]$Src, [string]$Dest, [int]$Size)
    $dir = Split-Path $Dest -Parent
    if ($dir -and -not (Test-Path $dir)) { New-Item -ItemType Directory -Force -Path $dir | Out-Null }
    $img = [System.Drawing.Image]::FromFile((Resolve-Path $Src))
    $bmp = New-Object System.Drawing.Bitmap $Size, $Size
    $g = [System.Drawing.Graphics]::FromImage($bmp)
    $g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $g.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality
    $g.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
    $g.Clear([System.Drawing.Color]::FromArgb(255, 22, 163, 74))
    $g.DrawImage($img, 0, 0, $Size, $Size)
    $g.Dispose()
    $bmp.Save($Dest, [System.Drawing.Imaging.ImageFormat]::Png)
    $bmp.Dispose()
    $img.Dispose()
    Write-Host "  $Size px -> $Dest"
}

if (-not (Test-Path $Source)) { Write-Error "Arquivo nao encontrado: $Source" }

$res = Join-Path $root "app\src\main\res"
Write-Host "Fonte: $Source"
Save-ResizedPng $Source (Join-Path $res "drawable-nodpi\ic_launcher_foreground.png") 432
foreach ($pair in @(
    @("mipmap-mdpi", 48), @("mipmap-hdpi", 72), @("mipmap-xhdpi", 96),
    @("mipmap-xxhdpi", 144), @("mipmap-xxxhdpi", 192)
)) {
    $folder = $pair[0]; $size = $pair[1]
    Save-ResizedPng $Source (Join-Path $res "$folder\ic_launcher.png") $size
    Save-ResizedPng $Source (Join-Path $res "$folder\ic_launcher_round.png") $size
}
Copy-Item $Source (Join-Path $root "app\designs\logo\corridometro-logo.png") -Force
Write-Host "Icone atualizado." -ForegroundColor Green
