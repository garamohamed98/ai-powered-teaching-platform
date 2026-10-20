import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserDropdownMenuComponent } from './user-dropdown-menu.component';

describe('UserDropdownMenuComponent', () => {
  let component: UserDropdownMenuComponent;
  let fixture: ComponentFixture<UserDropdownMenuComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserDropdownMenuComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserDropdownMenuComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
