import { ChangeDetectorRef, Component, ElementRef, OnInit, ViewChild } from '@angular/core';

import allFilters from '../filters';
import Filter from '../models/filter.model';
import ImageSnippet from '../models/image-snippet.model';

@Component({
  selector: 'app-post-edit',
  templateUrl: './post-edit.component.html',
  styleUrls: ['./post-edit.component.css'],
})
export class PostEditComponent implements OnInit {
  private readonly FILTERS_LOAD_INTERVAL_IN_MS = 50;

  files: ImageSnippet[] = [];
  currentFile!: ImageSnippet;
  isInFiltersTab = true;
  filters: Filter[] = [];
  @ViewChild('canvas', { static: false }) canvas!: ElementRef;
  @ViewChild('image', { static: false }) image!: ElementRef;
  filtersInterval!: NodeJS.Timeout;

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
    this.loadFilters();
  }

  onChangeTab(isInFiltersTab: boolean): void {
    this.isInFiltersTab = isInFiltersTab;

    if (isInFiltersTab) {
      this.loadFilters();
    }
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

  private loadFilters(): void {
    let index = 0;
    this.filters = [];
    this.filtersInterval = setInterval(() => {
      if (index < allFilters.length) {
        this.filters.push(allFilters[index++]);
      } else {
        clearInterval(this.filtersInterval);
      }
    }, this.FILTERS_LOAD_INTERVAL_IN_MS);
  }
}
