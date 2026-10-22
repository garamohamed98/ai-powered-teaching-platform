import {ComponentFixture, fakeAsync, flush, TestBed} from '@angular/core/testing';

import { UserDropdownMenuComponent } from './user-dropdown-menu.component';
import {AuthService} from '@auth0/auth0-angular';
import {BehaviorSubject} from 'rxjs';
import {By} from '@angular/platform-browser';
import {provideNoopAnimations} from '@angular/platform-browser/animations';


const FALL_BACK_NAME = 'Account name';

const MOCK_USER = {
  id:"1",
  name:"John Doe",
  email:"johndoe@gmail.com"
}

describe('UserDropdownMenuComponent', () => {
  let component: UserDropdownMenuComponent;
  let fixture: ComponentFixture<UserDropdownMenuComponent>;
  let authService: jasmine.SpyObj<AuthService>;
  let authUserSubject: BehaviorSubject<any>;
  let isAuthenticatedSubject: BehaviorSubject<boolean>;
  let isLoadingSubject: BehaviorSubject<boolean>;


  beforeEach(async () => {

    authUserSubject = new BehaviorSubject<any>(null);
    isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
    isLoadingSubject = new BehaviorSubject<boolean>(false);

    authService = jasmine.createSpyObj(
      'AuthService',
      ['loginWithRedirect','logout'],
      {
        user$: authUserSubject.asObservable(),
        isAuthenticated$: isAuthenticatedSubject.asObservable(),
        isLoading$: isLoadingSubject.asObservable(),
      }
    )

    await TestBed.configureTestingModule({
      imports: [UserDropdownMenuComponent],
      providers:[
        {provide: AuthService, useValue: authService},
        provideNoopAnimations()
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserDropdownMenuComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  describe('when user is authenticated',()=>{

    beforeEach(() => {
      authUserSubject.next(MOCK_USER);
      isAuthenticatedSubject.next(true);
      isLoadingSubject.next(false);
    })

    it('should show user name ',()=>{
      fixture.detectChanges();
      const userName = fixture.debugElement.query(By.css('[data-testid="user-name"]'));

      expect(userName).not.toBeNull();
      expect(userName.nativeElement.textContent.trim())
        .toEqual(MOCK_USER.name);
    })

    it('should fall back to default name when user has no name',()=>{
      authUserSubject.next({
        id:'1'
      });

      fixture.detectChanges();
      const userName = fixture.debugElement.query(By.css('[data-testid="user-name"]'));

      expect(userName).not.toBeNull();
      expect(userName.nativeElement.textContent.trim()).toEqual(FALL_BACK_NAME);
    })

    it('should fall back to default name when the user object is not available',()=>{
      authUserSubject.next(null)

      fixture.detectChanges();
      const userName = fixture.debugElement.query(By.css('[data-testid="user-name"]'));

      expect(userName).not.toBeNull();
      expect(userName.nativeElement.textContent.trim()).toEqual(FALL_BACK_NAME);
    })

    it('should show profile menu',()=>{
      fixture.detectChanges();
      const menu = fixture.debugElement.query(By.css('[data-testid="user-menu"]'));
      const button = fixture.debugElement.query(By.css('[data-testid="user-menu-button"]'));

      expect(menu).not.toBeNull();
      expect(button).not.toBeNull();
    })

    it('should not show authentication buttons',()=>{
      fixture.detectChanges();
      const loginButton = fixture.debugElement.query(By.css('[data-testid="login-button"]'));
      const signupButton = fixture.debugElement.query(By.css('[data-testid="signup-button"]'));

      expect(loginButton).toBeNull();
      expect(signupButton).toBeNull();
    })

    it('should not show skeleton',()=>{
      fixture.detectChanges();
      const skeleton = fixture.debugElement.query(By.css('p-skeleton'));

      expect(skeleton).toBeNull();
    })

    describe('menu button',()=>{

      const clickMenuButton = ()=>{
        fixture.detectChanges();

        fixture.debugElement.query(
          By.css('[data-testid="user-menu-button"] button')
        )
          .nativeElement.click();

        fixture.detectChanges();
        flush();
        fixture.detectChanges();
      }

      const clickLogoutButton = ()=>{
        fixture.detectChanges();

        const logout = document.querySelector('.logout-menu-item a') as HTMLElement;
        logout.click();

      }

      it('should open menu when clicked',fakeAsync(()=>{

        clickMenuButton();

        const logout = document.querySelector('.logout-menu-item')

        expect(logout).toBeTruthy();

      }))

      it('should close menu when clicked twice',fakeAsync(()=>{
        clickMenuButton();
        clickMenuButton();

        const logout = document.querySelector('.logout-menu-item')

        expect(logout).toBeNull();
      }))

      it('should call logout when logout button is clicked',fakeAsync(()=>{
        clickMenuButton();
        clickLogoutButton();

        expect(authService.logout).toHaveBeenCalled();
      }))

    })

  })

  describe('when user is loading',()=>{

    const mockUser = {
      id:"1",
      name:"John Doe",
      email:"johndoe@gmail.com"
    }

    beforeEach(() => {
      authUserSubject.next(mockUser);
      isAuthenticatedSubject.next(true);
      isLoadingSubject.next(true);
    })

    it('should not show user name ',()=>{
      fixture.detectChanges();
      const userName = fixture.debugElement.query(By.css('[data-testid="user-name"]'));

      expect(userName).toBeNull();
    })

    it('should not show menu',()=>{
      fixture.detectChanges();
      const menu = fixture.debugElement.query(By.css('[data-testid="user-menu"]'));
      const button = fixture.debugElement.query(By.css('[data-testid="user-menu-button"]'));

      expect(menu).toBeNull();
      expect(button).toBeNull();
    })

    it('should not show authentication buttons',()=>{
      fixture.detectChanges();
      const loginButton = fixture.debugElement.query(By.css('[data-testid="login-button"]'));
      const signupButton = fixture.debugElement.query(By.css('[data-testid="signup-button"]'));

      expect(loginButton).toBeNull();
      expect(signupButton).toBeNull();
    })

    it('should show skeleton',()=>{
      fixture.detectChanges();
      const skeleton = fixture.debugElement.query(By.css('p-skeleton'));

      expect(skeleton).not.toBeNull();
    })

  })

  describe('when user is not authenticated',()=>{

    beforeEach(() => {
      authUserSubject.next(null);
      isAuthenticatedSubject.next(false);
      isLoadingSubject.next(false);
    })

    it('should not show user name ',()=>{
      fixture.detectChanges();
      const userName = fixture.debugElement.query(By.css('[data-testid="user-name"]'));

      expect(userName).toBeNull();
    })

    it('should not show menu',()=>{
      fixture.detectChanges();
      const menu = fixture.debugElement.query(By.css('[data-testid="user-menu"]'));
      const button = fixture.debugElement.query(By.css('[data-testid="user-menu-button"]'));

      expect(menu).toBeNull();
      expect(button).toBeNull();
    })

    it('should show authentication buttons',()=>{
      fixture.detectChanges();
      const loginButton = fixture.debugElement.query(By.css('[data-testid="login-button"]'));
      const signupButton = fixture.debugElement.query(By.css('[data-testid="signup-button"]'));

      expect(loginButton).not.toBeNull();
      expect(signupButton).not.toBeNull();
    })

    it('should not show skeleton',()=>{
      fixture.detectChanges();
      const skeleton = fixture.debugElement.query(By.css('p-skeleton'));

      expect(skeleton).toBeNull();
    })

    describe('authentication buttons',()=>{
      const clickLoginButton = ()=>{
        fixture.detectChanges();

        fixture.debugElement.query(
          By.css('[data-testid="login-button"] button')
        )
          .nativeElement.click();

      }

      const clickSignupButton = ()=>{
        fixture.detectChanges();

        fixture.debugElement.query(
          By.css('[data-testid="signup-button"] button')
        )
          .nativeElement.click();
      }

      it('should call login with redirect  when login button is clicked',()=>{
        clickLoginButton();

        expect(authService.loginWithRedirect).toHaveBeenCalled();
      })

      it('should call login with redirect when signup button is clicked',()=>{
        clickSignupButton();

        expect(authService.loginWithRedirect).toHaveBeenCalled();
      })

    })
  })

  describe('state transitions', () => {
    it('should go from skeleton to the user menu once loading finishes and the user is authenticated', () => {
      authUserSubject.next(null);
      isAuthenticatedSubject.next(false);
      isLoadingSubject.next(true);
      fixture.detectChanges();
      expect(fixture.debugElement.query(By.css('p-skeleton'))).not.toBeNull();

      authUserSubject.next(MOCK_USER);
      isAuthenticatedSubject.next(true);
      isLoadingSubject.next(false);
      fixture.detectChanges();

      expect(fixture.debugElement.query(By.css('p-skeleton'))).toBeNull();
      expect(fixture.debugElement.query(By.css('[data-testid="user-name"]')).nativeElement.textContent.trim()).toEqual(MOCK_USER.name!);
      expect(fixture.debugElement.query(By.css('[data-testid="user-menu-button"]'))).not.toBeNull();
    });

    it('should go from skeleton to authentication buttons once loading finishes without a session', () => {
      authUserSubject.next(null);
      isAuthenticatedSubject.next(false);
      isLoadingSubject.next(true);
      fixture.detectChanges();

      isLoadingSubject.next(false);
      fixture.detectChanges();

      expect(fixture.debugElement.query(By.css('p-skeleton'))).toBeNull();
      expect(fixture.debugElement.query(By.css('[data-testid="login-button"]'))).not.toBeNull();
      expect(fixture.debugElement.query(By.css('[data-testid="signup-button"]'))).not.toBeNull();
    });

    it('should switch from the user menu to authentication buttons when the user logs out', () => {
      authUserSubject.next(MOCK_USER);
      isAuthenticatedSubject.next(true);
      isLoadingSubject.next(false);
      fixture.detectChanges();
      expect(fixture.debugElement.query(By.css('[data-testid="user-menu-button"]'))).not.toBeNull();

      authUserSubject.next(null);
      isAuthenticatedSubject.next(false);
      fixture.detectChanges();

      expect(fixture.debugElement.query(By.css('[data-testid="user-menu-button"]'))).toBeNull();
      expect(fixture.debugElement.query(By.css('[data-testid="user-name"]'))).toBeNull();
      expect(fixture.debugElement.query(By.css('[data-testid="login-button"]'))).not.toBeNull();
      expect(fixture.debugElement.query(By.css('[data-testid="signup-button"]'))).not.toBeNull();
    });

  });

});
