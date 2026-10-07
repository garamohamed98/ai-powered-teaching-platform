import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExerciseInfosComponent } from './exercise-infos.component';

describe('ExerciseInfosComponent', () => {
  let component: ExerciseInfosComponent;
  let fixture: ComponentFixture<ExerciseInfosComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExerciseInfosComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ExerciseInfosComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
