import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserAcquaintanceComponent } from './user-acquaintance.component';

describe('UserAcquaintanceComponent', () => {
  let component: UserAcquaintanceComponent;
  let fixture: ComponentFixture<UserAcquaintanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UserAcquaintanceComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UserAcquaintanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
