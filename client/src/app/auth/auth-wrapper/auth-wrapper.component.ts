import { animate, state, style, transition, trigger } from '@angular/animations';
import { Component, HostListener, OnInit } from '@angular/core';
import { AUTH_FORMS_WIDTH_TO_TWO_COLUMNS_CHANGE } from 'src/app/common/const.data';

import { AuthService } from '../services/auth.service';

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
    trigger('showErrors', [
      state(
        'correct',
        style({
          transform: 'translateY(-100%)',
        })
      ),
      state(
        'incorrect',
        style({
          transform: 'translateY(0%)',
        })
      ),
      transition('correct <=> incorrect', [animate('500ms ease-in-out')]),
    ]),
  ],
})
export class AuthWrapperComponent implements OnInit {
  private readonly SING_IN_IMAGE = { present: -100, hidden: 0 };
  private readonly SING_UP_IMAGE = { present: 100, hidden: 0 };

  isInLoginView = false; // TODO: Revert to true
  hasErrors = false;
  public imagesPresentLeftValue!: number;
  public imagesHiddenLeftValue!: number;
  errors: string[] = [];
  errorsLeftOffset!: string;

  constructor(private authService: AuthService) {
    this.authService.authError.subscribe((authError) => {
      this.errors = authError?.errors || [];
      this.hasErrors = this.errors.length > 0;
    });
  }

  ngOnInit(): void {
    this.imagesPresentLeftValue = this.SING_IN_IMAGE.present;
    this.imagesHiddenLeftValue = this.SING_IN_IMAGE.hidden;
    this.setErrorsLeftOffset();
  }

  onChangeView(): void {
    this.isInLoginView = !this.isInLoginView;
    this.imagesPresentLeftValue = (this.isInLoginView
      ? this.SING_IN_IMAGE
      : this.SING_UP_IMAGE
    ).present;
    this.imagesHiddenLeftValue = (this.isInLoginView
      ? this.SING_IN_IMAGE
      : this.SING_UP_IMAGE
    ).hidden;
    this.onHideErrorMessages();
  }

  onHideErrorMessages = (): void => this.authService.authError.next(null);

  @HostListener('window:resize', ['$event'])
  private onResize = (): void => this.setErrorsLeftOffset();

  private setErrorsLeftOffset(): void {
    this.errorsLeftOffset =
      window.innerWidth > AUTH_FORMS_WIDTH_TO_TWO_COLUMNS_CHANGE ? '50%' : '0';
  }
}
