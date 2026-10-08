import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FillInBlankContentComponent } from './fill-in-blank-content.component';

describe('FillInBlankContentComponent', () => {
  let component: FillInBlankContentComponent;
  let fixture: ComponentFixture<FillInBlankContentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FillInBlankContentComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FillInBlankContentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
