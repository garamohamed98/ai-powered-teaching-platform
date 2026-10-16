import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';

import { ExercisesTableComponent } from './exercises-table.component';

describe('ExercisesTableComponent', () => {
  let component: ExercisesTableComponent;
  let fixture: ComponentFixture<ExercisesTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExercisesTableComponent],
      providers: [provideNoopAnimations()]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ExercisesTableComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('maxRows', 5);
    fixture.componentRef.setInput('exercises', []);
    fixture.componentRef.setInput('loading', false);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
