import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateExerciseFormModalComponent } from './create-exercise-form-modal.component';

describe('CreateExerciseFormModalComponent', () => {
  let component: CreateExerciseFormModalComponent;
  let fixture: ComponentFixture<CreateExerciseFormModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateExerciseFormModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateExerciseFormModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
