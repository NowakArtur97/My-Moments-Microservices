import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ClickAndDragToScrollService } from 'src/app/common/services/click-and-drag-to-scroll.service';

import { PostService } from '../services/post.service';
import { MyPostsComponent } from './my-posts.component';

describe('MyPostsComponent', () => {
  let component: MyPostsComponent;
  let fixture: ComponentFixture<MyPostsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MyPostsComponent],
      imports: [HttpClientTestingModule, RouterTestingModule],
      providers: [PostService, ClickAndDragToScrollService],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MyPostsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
