import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';

import { FollowerService } from '../service/follower.service';
import { UserAcquaintanceComponent } from './user-acquaintance.component';

describe('UserAcquaintanceComponent', () => {
  let component: UserAcquaintanceComponent;
  let fixture: ComponentFixture<UserAcquaintanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UserAcquaintanceComponent],
      imports: [
        HttpClientTestingModule,
        RouterTestingModule,
        NoopAnimationsModule,
      ],
      providers: [FollowerService],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UserAcquaintanceComponent);
    component = fixture.componentInstance;
    component.user = {
      username: 'user',
      numberOfFollowing: 10,
      numberOfFollowers: 10,
      photo: 'image',
      isMutual: false,
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
