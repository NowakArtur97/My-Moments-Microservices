import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-auth-wrapper',
  templateUrl: './auth-wrapper.component.html',
  styleUrls: ['./auth-wrapper.component.css'],
})
export class AuthWrapperComponent implements OnInit {
  isInLoginView = true;

  constructor() {}

  ngOnInit(): void {}

  onChangeView(): void {
    this.isInLoginView = !this.isInLoginView;
  }
}
