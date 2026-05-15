import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CoursesFormModalComponent } from './courses-form-modal.component';

describe('CoursesFormModalComponent', () => {
  let component: CoursesFormModalComponent;
  let fixture: ComponentFixture<CoursesFormModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CoursesFormModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CoursesFormModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
