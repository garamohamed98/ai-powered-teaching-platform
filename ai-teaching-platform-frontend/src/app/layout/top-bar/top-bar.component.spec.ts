import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TopBarComponent } from './top-bar.component';
import {provideNoopAnimations} from '@angular/platform-browser/animations';
import {Component} from '@angular/core';
import {UserDropdownMenuComponent} from '../../features/users';
import {By} from '@angular/platform-browser';

@Component({ selector: 'app-user-dropdown-menu', template: '' })
class UserDropdownMenuStubComponent {}

describe('TopBarComponent', () => {
  let component: TopBarComponent;
  let fixture: ComponentFixture<TopBarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TopBarComponent],
      providers:[provideNoopAnimations()]
    })
      .overrideComponent(TopBarComponent,{
        remove:{imports:[UserDropdownMenuComponent]},
        add:{imports:[UserDropdownMenuStubComponent]}
      })
    .compileComponents();

    fixture = TestBed.createComponent(TopBarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the menubar', () => {
    expect(fixture.debugElement.query(By.css('p-menubar'))).not.toBeNull();
  });

  it('should render the user dropdown menu inside the menubar', () => {
    expect(
      fixture.debugElement.query(By.css('p-menubar app-user-dropdown-menu'))
    ).not.toBeNull();
  });
});
