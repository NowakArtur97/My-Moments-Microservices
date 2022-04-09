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

  @ViewChild('canvas', { static: false }) mainImageCanvas!: ElementRef;
  @ViewChild('image', { static: false }) mainImage!: ElementRef;
  @ViewChildren('filterCanvas', { read: ElementRef })
  filtersCanvases!: QueryList<ElementRef<HTMLCanvasElement>>;

  files: ImageSnippet[] = [];
  currentFile!: ImageSnippet;
  filters: Filter[] = [];
  mainImageContext!: CanvasRenderingContext2D;
  isInFiltersTab = true;

  filtersInterval!: NodeJS.Timeout;

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
    filter.apply(this.mainImageContext);
    this.mainImageContext.drawImage(
      this.mainImageCanvas.nativeElement,
      0,
      0,
      this.mainImageCanvas.nativeElement.width,
      this.mainImageCanvas.nativeElement.height
    );
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

  private loadFirstImage() {
    this.currentFile = this.files[0];
    this.changeDetectorRef.detectChanges();
    this.loadImageToCanvas(this.currentFile.src);
  }

  private loadImageToCanvas(src: string): void {
    this.changeDetectorRef.detectChanges();
    (this.mainImage.nativeElement as HTMLImageElement).src = src;
    const canvasElement: HTMLCanvasElement = this.mainImageCanvas.nativeElement;
    this.mainImageContext = canvasElement.getContext('2d')!;
    (this.mainImage.nativeElement as HTMLImageElement).onload = () => {
      this.mainImageContext.drawImage(
        this.mainImageCanvas.nativeElement,
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
        const filter = allFilters[index];
        this.filters.push(filter);
        this.changeDetectorRef.detectChanges();
        const filterCanvas = this.filtersCanvases.get(index)!;
        const context = filterCanvas.nativeElement.getContext('2d')!;
        filter.apply(context);
        context.drawImage(
          this.mainImage.nativeElement,
          0,
          0,
          filterCanvas.nativeElement.width,
          filterCanvas.nativeElement.height
        );
        index++;
      } else {
        clearInterval(this.filtersInterval);
      }
    }, this.FILTERS_LOAD_INTERVAL_IN_MS);
  }
}
