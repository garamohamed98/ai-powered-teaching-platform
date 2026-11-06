import {TestBed} from '@angular/core/testing';
import {provideHttpClient} from '@angular/common/http';
import {HttpTestingController, provideHttpClientTesting} from '@angular/common/http/testing';

import {CourseCategoryService} from './course-category.service';
import {CourseCategory} from '../models/course-category.model';

describe('CourseCategoryService', () => {
  let service: CourseCategoryService;
  let httpMock: HttpTestingController;

  const BASE_URL = 'http://localhost:8080/api/course-category';

  const MOCK_CATEGORIES = [
    {id: '1', name: 'Frontend'},
    {id: '2', name: 'Backend'},
  ] as unknown as CourseCategory[];

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
      ]
    });

    service = TestBed.inject(CourseCategoryService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getCourseCategoryList', () => {

    it('should send a GET request to the category endpoint', () => {
      service.getCourseCategoryList().subscribe();

      const req = httpMock.expectOne(BASE_URL);
      expect(req.request.method).toBe('GET');

      req.flush([]);
    });

    it('should return the categories sent by the server', () => {
      let result: CourseCategory[] | undefined;

      service.getCourseCategoryList().subscribe(categories => result = categories);

      httpMock.expectOne(BASE_URL).flush(MOCK_CATEGORIES);

      expect(result).toEqual(MOCK_CATEGORIES);
    });

    it('should propagate the error when the server fails', () => {
      let status: number | undefined;

      service.getCourseCategoryList().subscribe({
        error: err => status = err.status
      });

      httpMock.expectOne(BASE_URL).flush('Server error', {status: 500, statusText: 'Server Error'});

      expect(status).toBe(500);
    });

  });
});
