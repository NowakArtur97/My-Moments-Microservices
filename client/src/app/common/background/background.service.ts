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
  readonly NUMBER_OF_BACKUP_IMAGES_FOR_ANIMATION = 4;

  getRandomImages(): string[][] {
    const size = this.getSizeBasedOnResolution();
    const tilesSize = this.getTilesSize();
    const imagesPaths = this.getImagesPaths(size);
    return this.createTiles(tilesSize, imagesPaths);
  }

  private getImagesPaths = (size: string): string[] =>
    this.IMAGES_NAMES.map((name) => this.getImagePath(name, size));

  private getImagePath = (name: string, size: string): string =>
    `${this.IMAGES_PATH}${name}${this.IMAGES_SEPARATOR}${size}${this.IMAGES_EXTENSION}`;

  private createTiles(
    tilesSize: { width: number; height: number },
    imagesPaths: string[]
  ): string[][] {
    const images: string[][] = [];
    for (let column = 0; column < tilesSize.width; column++) {
      const nestedArray = [];
      for (
        let row = 0;
        row < tilesSize.height + this.NUMBER_OF_BACKUP_IMAGES_FOR_ANIMATION;
        row++
      ) {
        const image = this.toUrl(this.getRandomImage(imagesPaths));
        nestedArray.push(image);
      }
      images.push(nestedArray);
    }
    return images;
  }

  getTileHeight = (): number => window.innerHeight / this.getTilesSize().height;

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

  private toUrl = (image: string): string => `url(${image})`;

  private getSizeBasedOnResolution(): string {
    const windowWidth = window.innerWidth;
    if (windowWidth >= this.WINDOW_WIDTHS.big) {
      return this.IMAGE_SIZES.medium;
    } else {
      return this.IMAGE_SIZES.small;
    }
  }

  private getRandomImage = (imagesPaths: string[]): string =>
    imagesPaths[Math.floor(Math.random() * imagesPaths.length)];
}
