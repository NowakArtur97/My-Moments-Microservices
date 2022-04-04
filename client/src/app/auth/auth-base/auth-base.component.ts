import { animate, state, style, transition, trigger } from '@angular/animations';
import { Component, EventEmitter, Input, OnDestroy, OnInit, Output, ViewChild } from '@angular/core';
import { AbstractControl, NgForm } from '@angular/forms';
import { Subscription } from 'rxjs';

import { AuthService } from '../services/auth.service';

@Component({
  template: '',
  styleUrls: ['./auth-base.component.css'],
  animations: [
    trigger('switch', [
      state(
        'present',
        style({
          left: '{{presentLeftValue}}%',
        }),
        { params: { presentLeftValue: 0 } }
      ),
      state(
        'hidden',
        style({
          left: '{{hiddenLeftValue}}%',
        }),
        { params: { hiddenLeftValue: 0 } }
      ),
      transition('present <=> hidden', [animate('500ms ease-in-out')]),
    ]),
  ],
})
export abstract class AuthBaseComponent implements OnInit, OnDestroy {
  @ViewChild('authForm') authForm!: NgForm;

  private authErrorsSunscription$!: Subscription;
  public presentLeftValueInPercentage!: number;
  public hiddenLeftValueInPercentage!: number;

  @Output()
  private viewChanged: EventEmitter<void> = new EventEmitter<void>();
  @Input() isInLoginView!: boolean;

  constructor(protected authService: AuthService) {
    this.setupAnimationValues();
  }

  ngOnInit(): void {}

  ngOnDestroy = (): void => this.authErrorsSunscription$.unsubscribe();

  abstract onSubmit(): void;

  abstract setupAnimationValues(): void;

  onChangeView = (): void => this.viewChanged.emit();

  get username(): AbstractControl {
    const controlName = 'username';
    return this.authForm?.controls[controlName];
  }

  get email(): AbstractControl {
    const controlName = 'email';
    return this.authForm?.controls[controlName];
  }

  get password(): AbstractControl {
    const controlName = 'password';
    return this.authForm?.controls[controlName];
  }
}
