import { Directive, HostListener } from '@angular/core';

@Directive({
  selector: '[appOnClickStopPropagation]',
})
export class OnClickStopPropagationDirective {
  @HostListener('click', ['$event'])
  public onClick(event: any): void {
    event.stopPropagation();
  }
}
