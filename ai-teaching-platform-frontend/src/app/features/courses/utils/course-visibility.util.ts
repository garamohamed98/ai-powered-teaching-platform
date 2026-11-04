import {CourseVisibility} from '../models/course.model';

export function getVisibilityLabel(visibility: CourseVisibility): string {
  const labels: Record<CourseVisibility, string> = {
    PUBLIC: 'Public',
    PRIVATE: 'Private',
  };

  return labels[visibility];
}

export function getVisibilityIcon(visibility: CourseVisibility): string {
  const icons: Record<CourseVisibility, string> = {
    PUBLIC: 'pi pi-globe',
    PRIVATE: 'pi pi-lock',
  };

  return icons[visibility];
}
