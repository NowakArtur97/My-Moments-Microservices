import { ChangeDetectorRef, Component, ElementRef, OnInit, ViewChild } from '@angular/core';

import filters from '../filters';
import Filter from '../models/filter.model';
import ImageSnippet from '../models/image-snippet.model';

@Component({
  selector: 'app-post-edit',
  templateUrl: './post-edit.component.html',
  styleUrls: ['./post-edit.component.css'],
})
export class PostEditComponent implements OnInit {
  files: ImageSnippet[] = [];
  currentFile!: ImageSnippet;
  isInFiltersTab = true;
  filters: Filter[] = filters;
  @ViewChild('canvas', { static: false }) canvas!: ElementRef;
  @ViewChild('image', { static: false }) image!: ElementRef;

  constructor(private changeDetectorRef: ChangeDetectorRef) {}

  ngOnInit(): void {}

  onUploadImage(input: HTMLInputElement): void {
    const inputFiles = input.files;
    if (inputFiles) {
      const numberOfFiles = inputFiles.length;
      for (let index = 0; index < numberOfFiles; index++) {
        this.loadData(inputFiles[index]);
      }
    }
  }

  onChangeTab(isInFiltersTab: boolean): void {
    this.isInFiltersTab = isInFiltersTab;
  }

  private loadData(file: File): void {
    const fileReader = new FileReader();
    fileReader.onloadend = (event: any) => {
      const imageSnipper: ImageSnippet = {
        src: event.target.result,
        file,
      };
      this.files.push(imageSnipper);
      if (this.files.length === 1) {
        this.loadFirstImage();
      }
    };
    fileReader.readAsDataURL(file);
  }

  private loadFirstImage() {
    this.currentFile = this.files[0];
    this.changeDetectorRef.detectChanges();
    this.loadImageToCanvas(this.currentFile.src);
  }

  private loadImageToCanvas(src: string): void {
    this.changeDetectorRef.detectChanges();
    (this.image.nativeElement as HTMLImageElement).src = src;
    const canvasElement: HTMLCanvasElement = this.canvas.nativeElement;
    const context: CanvasRenderingContext2D = canvasElement.getContext('2d')!;
    (this.image.nativeElement as HTMLImageElement).onload = () => {
      context.drawImage(
        this.canvas.nativeElement,
        0,
        0,
        canvasElement.width,
        canvasElement.height
      );
    };
  }
}
