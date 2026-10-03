import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FillInBlankFormComponent } from './fill-in-blank-form.component';

describe('FillInBlankFormComponent', () => {
  let component: FillInBlankFormComponent;
  let fixture: ComponentFixture<FillInBlankFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FillInBlankFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FillInBlankFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
