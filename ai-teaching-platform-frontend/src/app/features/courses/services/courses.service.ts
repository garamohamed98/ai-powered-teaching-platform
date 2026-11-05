import {inject, Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Course} from '../models/course.model';
import {Observable} from 'rxjs';
import {Lesson} from '../models/lesson.model';
import {CreateCourseDto} from '../models/create-course.dto';
import {UpdateCourseDto} from '../models/update-course.dto';

@Injectable({
  providedIn: 'root'
})
export class CoursesService {

  private http = inject(HttpClient);
  private baseUrl = 'http://localhost:8080/api/course';

  constructor() {}

  getCourses() {
    console.log("fetching courses");
    return this.http.get<Course[]>(this.baseUrl);
  }

  getLessonsByCourseId(courseId: string) {
    console.log("fetching lessons list for course id: " + courseId)
    return this.http.get<Lesson[]>(`${this.baseUrl}/${courseId}/lesson`);
  }

  createCourse(createCourseDto: CreateCourseDto) {
    console.log("creating course: ",createCourseDto);
    return this.http.post<Course>(this.baseUrl, createCourseDto);
  }

  updateCourse(courseId: string, updateCourseDto: UpdateCourseDto) {
    console.log("update course: ",updateCourseDto);
    return this.http.patch(`${this.baseUrl}/${courseId}`, updateCourseDto);
  }

  deleteCourse(id: string) {
    console.log("deleting course id: ",id);
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  createLesson(courseId:string, lessonTitle:string){
    console.log("creating lesson: ",courseId," with lesson title: ",lessonTitle);
    const body = {
      title: lessonTitle
    }
    return this.http.post<Lesson>(`${this.baseUrl}/${courseId}/lesson`,body,{
      observe: 'response'
    })
  }
}
