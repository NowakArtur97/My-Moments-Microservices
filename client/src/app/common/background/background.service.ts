import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class BackgroundService {
  private readonly IMAGES_NAMES = ['astronaut', 'skate', 'winter'];
  private readonly IMAGE_SIZES = {
    small: 'small',
    medium: 'medium',
  };
  private readonly WINDOW_WIDTHS = {
    medium: 1920,
    big: 2560,
  };
  private readonly IMAGES_PATH = '../../../assets/pictures/';
  private readonly IMAGES_EXTENSION = '.jpg';
  private readonly IMAGES_SEPARATOR = '-';
  private readonly TILES_SIZES = {
    small: {
      width: 6,
      height: 6,
    },
    medium: {
      width: 8,
      height: 8,
    },
    big: {
      width: 12,
      height: 12,
    },
  };

  getRandomImages(): String[][] {
    const size = this.getSizeBasedOnResolution();
    const tilesSize = this.getTilesSize();
    const imagesPaths = this.getImagesPaths(size);
    return this.createTiles(tilesSize, imagesPaths);
  }

  private getImagesPaths = (size: String): String[] =>
    this.IMAGES_NAMES.map((name) => this.getImagePath(name, size));

  private getImagePath = (name: String, size: String): String =>
    `${this.IMAGES_PATH}${name}${this.IMAGES_SEPARATOR}${size}${this.IMAGES_EXTENSION}`;

  private createTiles(
    tilesSize: { width: number; height: number },
    imagesPaths: String[]
  ) {
    const images: String[][] = [];
    for (let row = 0; row < tilesSize.width; row++) {
      const nestedArray = [];
      for (let column = 0; column < tilesSize.height; column++) {
        const image = this.toUrl(
          imagesPaths[Math.floor(Math.random() * imagesPaths.length)]
        );
        nestedArray.push(image);
      }
      images.push(nestedArray);
    }
    return images;
  }

  getTileHeight = (): String =>
    `${window.innerHeight / this.getTilesSize().height}px`;

  getTilesSize(): { width: number; height: number } {
    const windowWidth = window.outerWidth;
    if (windowWidth >= this.WINDOW_WIDTHS.big) {
      return this.TILES_SIZES.big;
    } else if (windowWidth >= this.WINDOW_WIDTHS.medium) {
      return this.TILES_SIZES.medium;
    } else {
      return this.TILES_SIZES.small;
    }
  }

  private toUrl = (image: String): String => `url(${image})`;

  private getSizeBasedOnResolution(): String {
    const windowWidth = window.innerWidth;
    if (windowWidth >= this.WINDOW_WIDTHS.big) {
      return this.IMAGE_SIZES.medium;
    } else {
      return this.IMAGE_SIZES.small;
    }
  }
}
