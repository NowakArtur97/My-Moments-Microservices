import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';

import ImageSnippet from '../models/image-snippet.model';

@Component({
  selector: 'app-post-edit',
  templateUrl: './post-edit.component.html',
  styleUrls: ['./post-edit.component.css'],
})
export class PostEditComponent implements OnInit {
  private files: ImageSnippet[] = [];
  @ViewChild('canvas') canvas!: ElementRef;

  constructor() {}

  ngOnInit(): void {}

  onUploadImage(input: HTMLInputElement): void {
    const inputFiles = input.files;
    const previews = [];
    if (inputFiles) {
      const numberOfFiles = inputFiles.length;
      for (let i = 0; i < numberOfFiles; i++) {
        const fileReader = new FileReader();
        fileReader.onload = (event: any) => {
          const result = event.target.result;
          previews.push(result);
          const imageSnippet: ImageSnippet = {
            src: result,
            file: inputFiles[i],
          };
          this.files.push(imageSnippet);
          if (i == 0) {
            this.loadImageToCanvas(imageSnippet.src);
          }
        };
        fileReader.readAsDataURL(inputFiles[i]);
      }
    }
  }
  private loadImageToCanvas(src: string): void {
    const image = new Image();
    image.src = src;
    const canvasElement: HTMLCanvasElement = this.canvas.nativeElement;
    const context: CanvasRenderingContext2D = canvasElement.getContext('2d')!;
    image.onload = () => {
      context.drawImage(image, 0, 0, canvasElement.width, canvasElement.height);
    };
  }
}
