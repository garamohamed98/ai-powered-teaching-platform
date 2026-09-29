import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LessonsPanelComponent } from './lessons-panel.component';

describe('LessonsPanelComponent', () => {
  let component: LessonsPanelComponent;
  let fixture: ComponentFixture<LessonsPanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LessonsPanelComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LessonsPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
