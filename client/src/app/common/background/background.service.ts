import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class BackgroundService {
  private readonly IMAGES_NAMES = ['astronaut', 'skate', 'winter'];
  private readonly IMAGE_SIZES = {
    small: 'small',
    medium: 'medium',
    big: 'big',
  };
  private readonly WINDOW_SIZES = {
    medium: 1920,
    big: 2560,
  };
  private readonly IMAGES_PATH = '../../../assets/pictures/';
  private readonly IMAGES_EXTENSION = '.jpg';
  private readonly IMAGES_SEPARATOR = '-';
  private readonly NUMBER_OF_IMAGES = 20;

  getRandomImages(): String[] {
    const imagesPaths = this.getImagesPaths();
    const images = [];
    for (let i = 0; i < this.NUMBER_OF_IMAGES; i++) {
      images.push(imagesPaths[Math.floor(Math.random() * imagesPaths.length)]);
    }
    return images.map(this.toUrl);
  }

  private toUrl = (image: String): String => `url(${image})`;

  private getImagesPaths(): String[] {
    const size = this.getSizeBasedOnResolution();
    return this.IMAGES_NAMES.map((name) => this.getImagePath(name, size));
  }

  private getImagePath = (name: String, size: String): String =>
    `${this.IMAGES_PATH}${name}${this.IMAGES_SEPARATOR}${size}${this.IMAGES_EXTENSION}`;

  private getSizeBasedOnResolution(): String {
    const windowWidth = window.innerWidth;
    if (windowWidth >= this.WINDOW_SIZES.big) {
      return this.IMAGE_SIZES.medium;
    } else if (windowWidth >= this.WINDOW_SIZES.medium) {
      return this.IMAGE_SIZES.medium;
    } else {
      return this.IMAGE_SIZES.small;
    }
  }
}
