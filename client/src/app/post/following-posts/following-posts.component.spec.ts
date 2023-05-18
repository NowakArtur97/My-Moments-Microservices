import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ClickAndDragToScrollService } from 'src/app/common/services/click-and-drag-to-scroll.service';

import { PostService } from '../services/post.service';
import { FollowingPostsComponent } from './following-posts.component';

describe('FollowingPostsComponent', () => {
  let component: FollowingPostsComponent;
  let fixture: ComponentFixture<FollowingPostsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FollowingPostsComponent],
      imports: [HttpClientTestingModule, RouterTestingModule],
      providers: [PostService, ClickAndDragToScrollService],
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
