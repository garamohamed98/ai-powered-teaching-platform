import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LessonsFormModalComponent } from './lessons-form-modal.component';

describe('LessonsFormModalComponent', () => {
  let component: LessonsFormModalComponent;
  let fixture: ComponentFixture<LessonsFormModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LessonsFormModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LessonsFormModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
