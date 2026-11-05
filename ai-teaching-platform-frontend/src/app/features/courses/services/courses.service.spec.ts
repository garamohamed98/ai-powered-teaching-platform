import {TestBed} from '@angular/core/testing';
import {provideHttpClient} from '@angular/common/http';
import {HttpTestingController, provideHttpClientTesting} from '@angular/common/http/testing';

import {CoursesService} from './courses.service';
import {Course} from '../models/course.model';
import {CreateCourseDto} from '../models/create-course.dto';
import {UpdateCourseDto} from '../models/update-course.dto';

describe('CoursesService', () => {
  let service: CoursesService;
  let httpMock: HttpTestingController;

  const BASE_URL = 'http://localhost:8080/api/course';

  const MOCK_COURSES = [
    {id: '1', title: 'Angular Basics'},
    {id: '2', title: 'Spring Boot Deep Dive'},
  ] as unknown as Course[];

  const MOCK_CREATE_DTO = {
    title: 'Docker for Beginners',
  } as unknown as CreateCourseDto;

  const MOCK_UPDATE_DTO = {
    title: 'Updated Angular Title',
  } as unknown as UpdateCourseDto;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
      ]
    });

    service = TestBed.inject(CoursesService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getCourses', () => {

    it('should send a GET request to the course endpoint', () => {
      service.getCourses().subscribe();

      const req = httpMock.expectOne(BASE_URL);
      expect(req.request.method).toBe('GET');

      req.flush([]);
    });

    it('should return the courses sent by the server', () => {
      let result: Course[] | undefined;

      service.getCourses().subscribe(courses => result = courses);

      httpMock.expectOne(BASE_URL).flush(MOCK_COURSES);

      expect(result).toEqual(MOCK_COURSES);
    });

    it('should return an empty list when the server has no courses', () => {
      let result: Course[] | undefined;

      service.getCourses().subscribe(courses => result = courses);

      httpMock.expectOne(BASE_URL).flush([]);

      expect(result).toEqual([]);
    });

    it('should propagate the error when the server fails', () => {
      let status: number | undefined;

      service.getCourses().subscribe({
        error: err => status = err.status
      });

      httpMock.expectOne(BASE_URL).flush('Server error', {status: 500, statusText: 'Server Error'});

      expect(status).toBe(500);
    });

  });

  describe('createCourse', () => {

    it('should send a POST request to the course endpoint', () => {
      service.createCourse(MOCK_CREATE_DTO).subscribe();

      const req = httpMock.expectOne(BASE_URL);
      expect(req.request.method).toBe('POST');

      req.flush(MOCK_COURSES[0]);
    });

    it('should send the dto as the request body', () => {
      service.createCourse(MOCK_CREATE_DTO).subscribe();

      const req = httpMock.expectOne(BASE_URL);
      expect(req.request.body).toEqual(MOCK_CREATE_DTO);

      req.flush(MOCK_COURSES[0]);
    });

    it('should return the created course sent by the server', () => {
      let result: Course | undefined;

      service.createCourse(MOCK_CREATE_DTO).subscribe(course => result = course);

      httpMock.expectOne(BASE_URL).flush(MOCK_COURSES[0]);

      expect(result).toEqual(MOCK_COURSES[0]);
    });

    it('should propagate the error when the server rejects the course', () => {
      let status: number | undefined;

      service.createCourse(MOCK_CREATE_DTO).subscribe({
        error: err => status = err.status
      });

      httpMock.expectOne(BASE_URL).flush('Bad request', {status: 400, statusText: 'Bad Request'});

      expect(status).toBe(400);
    });

  });

  describe('deleteCourse', () => {

    it('should send a DELETE request to the course id endpoint', () => {
      service.deleteCourse('42').subscribe();

      const req = httpMock.expectOne(`${BASE_URL}/42`);
      expect(req.request.method).toBe('DELETE');

      req.flush(null);
    });

    it('should complete when the server answers with no content', () => {
      let completed = false;

      service.deleteCourse('42').subscribe({
        complete: () => completed = true
      });

      httpMock.expectOne(`${BASE_URL}/42`).flush(null, {status: 204, statusText: 'No Content'});

      expect(completed).toBeTrue();
    });

    it('should propagate the error when the course is not found', () => {
      let status: number | undefined;

      service.deleteCourse('42').subscribe({
        error: err => status = err.status
      });

      httpMock.expectOne(`${BASE_URL}/42`).flush('Not found', {status: 404, statusText: 'Not Found'});

      expect(status).toBe(404);
    });

  });

  describe('updateCourse', () => {

    it('should send a PATCH request to the course id endpoint', () => {
      service.updateCourse('42', MOCK_UPDATE_DTO).subscribe();

      const req = httpMock.expectOne(`${BASE_URL}/42`);
      expect(req.request.method).toBe('PATCH');

      req.flush(MOCK_COURSES[0]);
    });

    it('should send the update dto as the request body', () => {
      service.updateCourse('42', MOCK_UPDATE_DTO).subscribe();

      const req = httpMock.expectOne(`${BASE_URL}/42`);
      expect(req.request.body).toEqual(MOCK_UPDATE_DTO);

      req.flush(MOCK_COURSES[0]);
    });

    it('should return the updated course sent by the server', () => {
      let result: Course | undefined;

      service.updateCourse('42', MOCK_UPDATE_DTO).subscribe(course => result = course as Course);

      httpMock.expectOne(`${BASE_URL}/42`).flush(MOCK_COURSES[0]);

      expect(result).toEqual(MOCK_COURSES[0]);
    });

    it('should propagate the error when the server fails during update', () => {
      let status: number | undefined;

      service.updateCourse('42', MOCK_UPDATE_DTO).subscribe({
        error: err => status = err.status
      });

      httpMock.expectOne(`${BASE_URL}/42`).flush('Not found', {status: 404, statusText: 'Not Found'});

      expect(status).toBe(404);
    });

  });

});
