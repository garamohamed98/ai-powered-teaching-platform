import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MainLayoutComponent } from './main-layout.component';
import {Component} from '@angular/core';
import {provideRouter, RouterOutlet} from '@angular/router';
import {TopBarComponent} from '../top-bar/top-bar.component';
import {By} from '@angular/platform-browser';

@Component({ selector: 'app-top-bar', template: '' })
class TopBarStubComponent {}

describe('MainLayoutComponent', () => {
  let component: MainLayoutComponent;
  let fixture: ComponentFixture<MainLayoutComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MainLayoutComponent],
      providers: [provideRouter([])]
    }).overrideComponent(MainLayoutComponent, {
      remove:{imports: [TopBarComponent]},
      add:{imports: [TopBarStubComponent]},
    })
    .compileComponents();

    fixture = TestBed.createComponent(MainLayoutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the top bar', () => {
    expect(fixture.debugElement.query(By.css('app-top-bar'))).not.toBeNull();
  });

  it('should render a router outlet inside main', () => {
    const outlet = fixture.debugElement.query(By.css('main router-outlet'));

    expect(outlet).not.toBeNull();
    expect(outlet.injector.get(RouterOutlet)).toBeTruthy();
  });

});
