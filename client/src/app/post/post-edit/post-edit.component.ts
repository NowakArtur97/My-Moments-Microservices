import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';

import ImageSnippet from '../models/image-snippet.model';

@Component({
  selector: 'app-post-edit',
  templateUrl: './post-edit.component.html',
  styleUrls: ['./post-edit.component.css'],
})
export class PostEditComponent implements OnInit {
  files: ImageSnippet[] = [];
  @ViewChild('canvas') canvas!: ElementRef;

  constructor() {}

  ngOnInit(): void {}

  onUploadImage(input: HTMLInputElement): void {
    const inputFiles = input.files;
    if (inputFiles) {
      const numberOfFiles = inputFiles.length;
      for (let index = 0; index < numberOfFiles; index++) {
        this.loadData(inputFiles, index);
        if (index === 0) {
          this.loadImageToCanvas(this.files[index].src);
        }
      }
    }
  }

  private loadData(inputFiles: FileList, index: number): void {
    const fileReader = new FileReader();
    fileReader.onload = (event: any) => {
      this.files.push({
        src: event.target.result,
        file: inputFiles[index],
      });
    };
    fileReader.readAsDataURL(inputFiles[index]);
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
