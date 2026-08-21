import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CourseExercisesListComponent } from './course-exercises-list.component';

describe('CourseExercisesListComponent', () => {
  let component: CourseExercisesListComponent;
  let fixture: ComponentFixture<CourseExercisesListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CourseExercisesListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CourseExercisesListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
