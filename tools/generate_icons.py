#!/usr/bin/env python3
"""Regenerates KeyDrop's raster launcher icons and Play Store graphics.

The adaptive icon (API 26+) is defined by the vector drawables in
app/src/main/res/drawable/ic_launcher_*.xml. This script renders the matching
bitmaps that Android 7.0-8.0 launchers need, plus the Play Store listing assets,
so every artwork surface stays in sync with one source of truth.

Usage:  python3 tools/generate_icons.py       (requires Pillow)
"""

import os

from PIL import Image, ImageDraw, ImageFont

REPO_ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
RES_DIR = os.path.join(REPO_ROOT, "app", "src", "main", "res")
STORE_DIR = os.path.join(REPO_ROOT, "docs", "store")

BACKGROUND = (6, 21, 46, 255)
KEYBOARD_BODY = (39, 54, 79, 255)
KEY = (244, 247, 250, 255)
ACCENT = (25, 185, 242, 255)

# Densities for legacy (pre-adaptive-icon) launchers, in pixels.
LAUNCHER_DENSITIES = {
    "mdpi": 48,
    "hdpi": 72,
    "xhdpi": 96,
    "xxhdpi": 144,
    "xxxhdpi": 192,
}

SUPERSAMPLE = 4

# Artwork is authored on the 108x108 grid shared with the vector drawables.
GRID = 108.0
CORNER_RADIUS = 18.0
KEYBOARD_BOX = (12.0, 24.0, 96.0, 72.0)
KEYBOARD_RADIUS = 7.0
KEY_ROWS = [
    (33.0, 40.0, [(20.0, 28.0), (33.0, 41.0), (46.0, 54.0), (59.0, 67.0),
                  (72.0, 80.0), (85.0, 89.0)]),
    (45.0, 52.0, [(24.0, 32.0), (37.0, 45.0), (50.0, 58.0), (63.0, 71.0),
                  (76.0, 84.0)]),
    (57.0, 64.0, [(20.0, 30.0), (35.0, 73.0), (78.0, 88.0)]),
]
CHEVRON = [(35.0, 80.0), (41.0, 74.0), (54.0, 86.0), (67.0, 74.0),
           (73.0, 80.0), (54.0, 99.0)]

FONT_CANDIDATES = [
    "/usr/share/fonts/truetype/liberation/LiberationSans-Bold.ttf",
    "/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf",
]


def draw_symbol(draw, size, offset_x=0.0, offset_y=0.0, scale=1.0):
    """Draws the keyboard and chevron symbol onto a canvas of the given size."""
    unit = size / GRID

    def point(x, y):
        return (offset_x + x * unit * scale, offset_y + y * unit * scale)

    def box(x0, y0, x1, y1):
        return [point(x0, y0), point(x1, y1)]

    left, top, right, bottom = KEYBOARD_BOX
    draw.rounded_rectangle(box(left, top, right, bottom),
                           radius=KEYBOARD_RADIUS * unit * scale,
                           fill=KEYBOARD_BODY)

    for row_top, row_bottom, keys in KEY_ROWS:
        for key_left, key_right in keys:
            draw.rectangle(box(key_left, row_top, key_right, row_bottom), fill=KEY)

    draw.polygon([point(x, y) for x, y in CHEVRON], fill=ACCENT)


def render_icon(size, shape):
    """Renders a launcher icon. Shape is 'rounded', 'circle', or 'square'."""
    canvas = size * SUPERSAMPLE
    image = Image.new("RGBA", (canvas, canvas), (0, 0, 0, 0))
    draw = ImageDraw.Draw(image)

    if shape == "circle":
        draw.ellipse([(0, 0), (canvas - 1, canvas - 1)], fill=BACKGROUND)
    elif shape == "rounded":
        draw.rounded_rectangle([(0, 0), (canvas - 1, canvas - 1)],
                               radius=CORNER_RADIUS * canvas / GRID, fill=BACKGROUND)
    else:
        draw.rectangle([(0, 0), (canvas - 1, canvas - 1)], fill=BACKGROUND)

    # Round icons mask the corners, so the symbol is inset to stay clear of them and
    # nudged up so the drawn artwork, rather than the 108x108 grid, sits centred.
    symbol_scale = 0.82 if shape == "circle" else 1.0
    inset = canvas * (1 - symbol_scale) / 2
    centring = 0.0
    if shape == "circle":
        symbol_centre_y = (KEYBOARD_BOX[1] + CHEVRON[-1][1]) / 2
        centring = (GRID / 2 - symbol_centre_y) * canvas / GRID * symbol_scale
    draw_symbol(draw, canvas, offset_x=inset, offset_y=inset + centring, scale=symbol_scale)

    return image.resize((size, size), Image.LANCZOS)


def load_font(size):
    for path in FONT_CANDIDATES:
        if os.path.exists(path):
            return ImageFont.truetype(path, size)
    return ImageFont.load_default()


def render_feature_graphic(width=1024, height=500):
    image = Image.new("RGB", (width, height), BACKGROUND[:3])
    draw = ImageDraw.Draw(image)

    # Subtle vertical wash so the flat brand navy does not read as a dead rectangle.
    for y in range(height):
        blend = y / float(height)
        draw.line([(0, y), (width, y)], fill=(
            int(BACKGROUND[0] + (18 - BACKGROUND[0]) * blend * 0.6),
            int(BACKGROUND[1] + (46 - BACKGROUND[1]) * blend * 0.6),
            int(BACKGROUND[2] + (86 - BACKGROUND[2]) * blend * 0.6),
        ))

    icon = render_icon(260, "rounded")
    image.paste(icon, (80, (height - 260) // 2), icon)

    title_font = load_font(84)
    tagline_font = load_font(34)
    note_font = load_font(27)

    text_x = 400
    draw.text((text_x, 168), "KeyDrop", font=title_font, fill=KEY[:3])
    draw.text((text_x, 266), "Keep your keyboard.", font=tagline_font, fill=ACCENT[:3])
    draw.text((text_x, 310), "Add the button Android forgot.", font=tagline_font,
              fill=ACCENT[:3])
    draw.text((text_x, 366), "No internet permission. No data collected.",
              font=note_font, fill=(185, 198, 216))

    return image


def write(image, path):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    image.save(path, "PNG", optimize=True)
    print("wrote", os.path.relpath(path, REPO_ROOT))


def main():
    for density, size in LAUNCHER_DENSITIES.items():
        directory = os.path.join(RES_DIR, "mipmap-" + density)
        write(render_icon(size, "rounded"), os.path.join(directory, "ic_launcher.png"))
        write(render_icon(size, "circle"), os.path.join(directory, "ic_launcher_round.png"))

    write(render_icon(512, "square"), os.path.join(STORE_DIR, "play-icon-512.png"))
    write(render_feature_graphic(), os.path.join(STORE_DIR, "feature-graphic-1024x500.png"))


if __name__ == "__main__":
    main()
