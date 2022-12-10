import { AfterViewChecked, Component, ElementRef, Input, OnChanges, OnInit, Renderer2, ViewChild } from '@angular/core';

import PostElement from '../models/post-element.model';

@Component({
  selector: 'app-post',
  templateUrl: './post.component.html',
  styleUrls: ['./post.component.css'],
})
export class PostComponent implements OnInit, OnChanges, AfterViewChecked {
  @Input() post!: PostElement;
  @ViewChild('postElement') postElement!: ElementRef<HTMLDivElement>;

  private readonly POST_TRANSFORM_SCALE = {
    active: 1.2,
    inactive: 0.6,
  };

  constructor(private renderer: Renderer2) {}

  ngOnInit(): void {}

  ngAfterViewChecked(): void {
    this.setupStyles();
  }

  ngOnChanges(): void {
    this.setupStyles();
  }

  onShowComments(): void {
    this.setTransformScale(this.POST_TRANSFORM_SCALE.inactive);
    console.log('A');
  }

  onChangeCurrentPhoto(direction: number): void {
    const post = this.post;
    const currentPhotoIndex = post.currentPhotoIndex;
    if (direction === 1 && currentPhotoIndex < post.photos.length - 1) {
      post.currentPhotoIndex = currentPhotoIndex + 1;
    } else if (direction === -1 && currentPhotoIndex > 0) {
      post.currentPhotoIndex = currentPhotoIndex - 1;
    }
  }

  private setupStyles(): void {
    if (this.postElement === undefined) {
      return;
    }
    if (this.post.isCurrentlyLastElement) {
      this.fixPaddingOfLastElement();
    }
    if (this.post.isActive) {
      this.setTransformScale(this.POST_TRANSFORM_SCALE.active);
    } else {
      this.setTransformScale(this.POST_TRANSFORM_SCALE.inactive);
    }
  }

  private setTransformScale = (scale: number): void =>
    this.renderer.setStyle(
      this.postElement.nativeElement,
      'transform',
      `scale(${scale})`
    );

  private fixPaddingOfLastElement(): void {
    const lastPost: HTMLDivElement = this.postElement.nativeElement;
    this.renderer.setStyle(lastPost, 'padding-right', '22vw');
    const changeButtonsWrapperChildren =
      lastPost.children[lastPost.children.length - 1].children;
    const leftChangePhotoButton = changeButtonsWrapperChildren[0];
    this.renderer.setStyle(leftChangePhotoButton, 'left', 'calc(2% + 11vw)');
    const rightChangePhotoButton = changeButtonsWrapperChildren[1];
    this.renderer.setStyle(rightChangePhotoButton, 'right', 'calc(2% + 11vw)');
  }
}
