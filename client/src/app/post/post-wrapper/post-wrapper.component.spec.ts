import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PostWrapperComponent } from './post-wrapper.component';

describe('PostWrapperComponent', () => {
  let component: PostWrapperComponent;
  let fixture: ComponentFixture<PostWrapperComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PostWrapperComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PostWrapperComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
