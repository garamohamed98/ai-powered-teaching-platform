import {CourseCategory} from './course-category.model';

export type CourseVisibility = 'PUBLIC' | 'PRIVATE';

export interface Course {
  id: string;
  title: string;
  category: CourseCategory;
  visibility: CourseVisibility;
  createdAt: string;
}
