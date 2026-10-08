import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExerciseAttemptComponent } from './exercise-attempt.component';

describe('ExerciseAttemptComponent', () => {
  let component: ExerciseAttemptComponent;
  let fixture: ComponentFixture<ExerciseAttemptComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExerciseAttemptComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ExerciseAttemptComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
