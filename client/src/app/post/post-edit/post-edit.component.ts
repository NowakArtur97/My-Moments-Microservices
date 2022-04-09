import { ChangeDetectorRef, Component, ElementRef, OnInit, QueryList, ViewChild, ViewChildren } from '@angular/core';

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

  @ViewChild('image', { static: false }) mainImage!: ElementRef;
  @ViewChild('canvas', { static: false }) mainImageCanvas!: ElementRef;
  @ViewChildren('filterCanvas', { read: ElementRef })
  filtersCanvases!: QueryList<ElementRef<HTMLCanvasElement>>;

  files: ImageSnippet[] = [];
  currentFile!: ImageSnippet;
  filters: Filter[] = [];
  mainCanvasElement!: HTMLCanvasElement;
  mainCanvasContext!: CanvasRenderingContext2D;
  isInFiltersTab = true;

  filtersInterval!: any;

  constructor(private changeDetectorRef: ChangeDetectorRef) {}

  ngOnInit(): void {}

  onUploadImage(imageInput: HTMLInputElement): void {
    const inputFiles = imageInput.files;
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

  onApplyFilter(filter: Filter): void {
    filter.apply(this.mainCanvasContext);
    this.drawImageOnMainCanvasContext();
  }

  private loadData(file: File): void {
    const fileReader = new FileReader();
    fileReader.onloadend = (event: any) => {
      const imageSnippet: ImageSnippet = {
        src: event.target.result,
        file,
      };
      this.files.push(imageSnippet);
      if (this.files.length === 1) {
        this.loadFirstImage();
      }
    };
    fileReader.readAsDataURL(file);
  }

  private loadFirstImage(): void {
    this.currentFile = this.files[0];
    this.changeDetectorRef.detectChanges();
    this.loadImageToCanvas();
  }

  private loadImageToCanvas(): void {
    this.changeDetectorRef.detectChanges();
    (this.mainImage
      .nativeElement as HTMLImageElement).src = this.currentFile.src;
    this.mainCanvasElement = this.mainImageCanvas.nativeElement;
    this.mainCanvasContext = this.mainCanvasElement.getContext('2d')!;
    (this.mainImage.nativeElement as HTMLImageElement).onload = () => {
      this.drawImageOnMainCanvasContext();
    };
  }

  private loadFilters(): void {
    let index = 0;
    this.filters = [];
    this.filtersInterval = setInterval(() => {
      if (index < allFilters.length) {
        const filter = allFilters[index];
        this.filters.push(filter);
        this.changeDetectorRef.detectChanges();
        const filterCanvas = this.filtersCanvases.get(index)!;
        const context = filterCanvas.nativeElement.getContext('2d')!;
        filter.apply(context);
        this.drawImageOnCanvasContext(context, filterCanvas.nativeElement);
        index++;
      } else {
        clearInterval(this.filtersInterval);
      }
    }, this.FILTERS_LOAD_INTERVAL_IN_MS);
  }

  private drawImageOnMainCanvasContext = (): void =>
    this.drawImageOnCanvasContext(
      this.mainCanvasContext,
      this.mainCanvasElement
    );

  private drawImageOnCanvasContext = (
    context: CanvasRenderingContext2D,
    canvasElement: HTMLCanvasElement
  ): void =>
    context.drawImage(
      this.mainImage.nativeElement,
      0,
      0,
      canvasElement.width,
      canvasElement.height
    );
}
