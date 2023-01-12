import { ChangeDetectorRef, Component, ElementRef, OnInit, QueryList, ViewChild, ViewChildren } from '@angular/core';

import ALL_EDITOR_FILTERS from '../data/editor.filters.data';
import ALL_EDITOR_SLIDERS from '../data/editor.sliders.data';
import EditorFilter from '../models/editor-slider.model';
import Filter from '../models/filter.model';
import ImageSnippet from '../models/image-snippet.model';
import { PostService } from '../services/post.service';

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

  currentPhotoIndex = 0;
  files: ImageSnippet[] = [];
  filters: Filter[] = [];
  editorSliders: EditorFilter[] = [];
  mainCanvasElement!: HTMLCanvasElement;
  mainCanvasContext!: CanvasRenderingContext2D;
  isInFiltersTab = false;

  filtersInterval!: any;

  constructor(
    private postServce: PostService,
    private changeDetectorRef: ChangeDetectorRef
  ) {}

  ngOnInit(): void {}

  onAddImages(imageInput: HTMLInputElement): void {
    const inputFiles = imageInput.files;
    if (inputFiles) {
      const numberOfFiles = inputFiles.length;
      for (let index = 0; index < numberOfFiles; index++) {
        this.loadData(inputFiles[index]);
      }
    }
    if (this.isInFiltersTab) {
      this.loadFilters();
    }
  }

  onChangeTab(isInFiltersTab: boolean): void {
    this.isInFiltersTab = isInFiltersTab;
    if (isInFiltersTab) {
      this.loadFilters();
    }
  }

  onApplyFilter(filter: Filter): void {
    // this.clearContext();
    // filter.apply(this.mainCanvasContext);
    // this.drawImageOnMainCanvasContext();
  }

  onChangeSliderValue(editorSlider: EditorFilter, value: number): void {
    const currentFile = this.files[this.currentPhotoIndex];
    const filters = currentFile.editorSliders;
    const changedFilter = filters.find(
      (filter) => filter.name === editorSlider.name
    )!!;
    changedFilter.currentValue = value;
    this.loadImage();
  }

  onCreatePost(): void {
    this.postServce.createPost(this.files);
  }

  onChangeCurrentPhoto(value: number): void {
    this.currentPhotoIndex += value;
    this.loadImage();
  }

  private loadData(file: File): void {
    const fileReader = new FileReader();
    fileReader.onloadend = (event: any) => {
      const sliders = ALL_EDITOR_SLIDERS.map((slider) => {
        return { ...slider } as EditorFilter;
      });
      const imageSnippet: ImageSnippet = {
        src: event.target.result,
        file,
        editorSliders: sliders,
      };
      this.files.push(imageSnippet);
      if (this.files.length === 1) {
        this.currentPhotoIndex = 0;
        this.loadImage();
      }
    };
    fileReader.readAsDataURL(file);
  }

  private loadImage(): void {
    this.editorSliders = this.files[this.currentPhotoIndex].editorSliders;
    this.changeDetectorRef.detectChanges();
    this.loadImageToCanvas();
  }

  private loadImageToCanvas(): void {
    this.changeDetectorRef.detectChanges();
    (this.mainImage.nativeElement as HTMLImageElement).src = this.files[
      this.currentPhotoIndex
    ].src;
    this.mainCanvasElement = this.mainImageCanvas.nativeElement;
    this.mainCanvasContext = this.mainCanvasElement.getContext('2d')!;
    const currentFile = this.files[this.currentPhotoIndex];
    const canvasFilters = currentFile.editorSliders
      .map((filter) => `${filter.name}(${filter.currentValue}${filter.unit})`)
      .join(' ');
    this.clearContext();
    this.mainCanvasContext.filter = canvasFilters;
    (this.mainImage.nativeElement as HTMLImageElement).onload = () => {
      this.drawImageOnMainCanvasContext();
    };
  }

  private loadFilters(): void {
    let index = 0;
    this.filters = [];
    this.filtersInterval = setInterval(() => {
      const allFilters = ALL_EDITOR_FILTERS;
      if (index < allFilters.length) {
        const filter = allFilters[index];
        this.filters.push(filter);
        this.changeDetectorRef.detectChanges();
        const filterCanvas = this.filtersCanvases.get(index)!;
        const filterContext = filterCanvas.nativeElement.getContext('2d')!;
        filterContext.filter = filter.value;
        this.drawImageOnCanvasContext(
          filterContext,
          filterCanvas.nativeElement
        );
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

  private clearContext = (): void =>
    this.mainCanvasContext.clearRect(
      0,
      0,
      this.mainCanvasElement.width,
      this.mainCanvasElement.height
    );
}
