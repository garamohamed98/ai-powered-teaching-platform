import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GenerateExerciseFormModalComponent } from './generate-exercise-form-modal.component';

describe('GenerateExerciseFormModalComponent', () => {
  let component: GenerateExerciseFormModalComponent;
  let fixture: ComponentFixture<GenerateExerciseFormModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GenerateExerciseFormModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GenerateExerciseFormModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
