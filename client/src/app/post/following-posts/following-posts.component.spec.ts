import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FollowingPostsComponent } from './following-posts.component';

describe('FollowingPostsComponent', () => {
  let component: FollowingPostsComponent;
  let fixture: ComponentFixture<FollowingPostsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FollowingPostsComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FollowingPostsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
