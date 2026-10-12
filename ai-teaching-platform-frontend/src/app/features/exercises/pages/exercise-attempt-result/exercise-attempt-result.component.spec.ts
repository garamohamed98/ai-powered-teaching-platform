import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExerciseAttemptResultComponent } from './exercise-attempt-result.component';

describe('ExerciseAttemptResultComponent', () => {
  let component: ExerciseAttemptResultComponent;
  let fixture: ComponentFixture<ExerciseAttemptResultComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExerciseAttemptResultComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ExerciseAttemptResultComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
