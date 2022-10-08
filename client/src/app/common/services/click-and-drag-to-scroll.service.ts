import { ElementRef, Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class ClickAndDragToScrollService {
  scrolledElement: ElementRef<HTMLDivElement> | undefined;

  private WALK_SPEED = 1.5;
  isScrolling = false;
  private startXPosition = 0;
  private scrollLeftPosition = 0;

  startScroll(event: MouseEvent): void {
    this.isScrolling = true;
    const { scrollLeft, offsetLeft } = this.scrolledElement!!.nativeElement;
    this.startXPosition = event.pageX - offsetLeft;
    this.scrollLeftPosition = scrollLeft;
  }

  stopScroll(): void {
    this.isScrolling = false;
  }

  dragAndScroll(event: MouseEvent): void {
    event.preventDefault();
    if (!this.isScrolling) {
      return;
    }
    const scrolledElement = this.scrolledElement!!.nativeElement;
    const xPosition = event.pageX - scrolledElement.offsetLeft;
    const walk = (xPosition - this.startXPosition) * this.WALK_SPEED;
    scrolledElement.scrollLeft = this.scrollLeftPosition - walk;
  }
}
