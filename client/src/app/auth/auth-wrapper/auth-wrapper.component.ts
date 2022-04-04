import { animate, state, style, transition, trigger } from '@angular/animations';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-auth-wrapper',
  templateUrl: './auth-wrapper.component.html',
  styleUrls: ['./auth-wrapper.component.css'],
  animations: [
    trigger('switch', [
      state(
        'present',
        style({
          transform: 'translateX({{presentLeftValue}}%)',
        }),
        { params: { presentLeftValue: 0 } }
      ),
      state(
        'hidden',
        style({
          transform: 'translateX({{hiddenLeftValue}}%)',
        }),
        { params: { hiddenLeftValue: 0 } }
      ),
      transition('present <=> hidden', [animate('500ms ease-in-out')]),
    ]),
  ],
})
export class AuthWrapperComponent implements OnInit {
  private readonly SING_IN_IMAGE = { present: -100, hidden: 0 };
  private readonly SING_UP_IMAGE = { present: 100, hidden: 0 };

  isInLoginView = true;
  public presentLeftValue!: number;
  public hiddenLeftValue!: number;

  constructor() {}

  ngOnInit(): void {
    this.presentLeftValue = this.SING_IN_IMAGE.present;
    this.hiddenLeftValue = this.SING_IN_IMAGE.hidden;
  }

  onChangeView(): void {
    this.isInLoginView = !this.isInLoginView;
    this.presentLeftValue = (this.isInLoginView
      ? this.SING_IN_IMAGE
      : this.SING_UP_IMAGE
    ).present;
    this.hiddenLeftValue = (this.isInLoginView
      ? this.SING_IN_IMAGE
      : this.SING_UP_IMAGE
    ).hidden;
  }
}
