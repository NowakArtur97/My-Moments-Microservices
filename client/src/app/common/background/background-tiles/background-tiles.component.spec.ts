import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BackgroundTilesComponent } from './background-tiles.component';

describe('BackgroundTilesComponent', () => {
  let component: BackgroundTilesComponent;
  let fixture: ComponentFixture<BackgroundTilesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [BackgroundTilesComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BackgroundTilesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
