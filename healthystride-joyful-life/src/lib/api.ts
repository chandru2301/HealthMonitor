import { toast } from 'sonner';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

export interface User {
  id?: number;
  name: string;
  email: string;
  dateOfBirth: string;
  gender: 'MALE' | 'FEMALE' | 'OTHER';
  heightCm: number;
  weightKg: number;
  activityLevel?: 'SEDENTARY' | 'LIGHTLY_ACTIVE' | 'MODERATELY_ACTIVE' | 'VERY_ACTIVE' | 'EXTRA_ACTIVE';
}

export interface HealthMetrics {
  id?: number;
  date: string;
  steps?: number;
  caloriesConsumed?: number;
  caloriesBurned?: number;
  distanceKm?: number;
  activeMinutes?: number;
  waterIntakeLiters?: number;
  sleepHours?: number;
  heartRateAvg?: number;
  netCalories?: number;
}

export interface Activity {
  id?: number;
  activityType: string;
  startTime: string;
  endTime: string;
  durationMinutes?: number;
  caloriesBurned?: number;
  distanceKm?: number;
  notes?: string;
  averagePace?: number;
}

export interface WeeklyStats {
  startDate: string;
  endDate: string;
  totalSteps: number;
  totalCaloriesBurned: number;
  totalCaloriesConsumed: number;
  netCalories: number;
  totalDistanceKm: number;
  totalActiveMinutes: number;
  averageStepsPerDay: number;
  averageActiveMinutesPerDay: number;
}

export interface DashboardSummary {
  bmr: number;
  tdee: number;
  bmi: number;
  age: number;
}

/**
 * Helper function to handle API responses with proper error handling
 */
async function handleResponse<T>(response: Response, showToast = true): Promise<T> {
  if (!response.ok) {
    let errorMessage = 'An error occurred';
    
    try {
      const errorData = await response.json();
      errorMessage = errorData.message || errorData.error || errorMessage;
    } catch {
      // If response is not JSON, use status text
      errorMessage = response.statusText || `HTTP ${response.status}`;
    }
    
    // Show toast notification for errors
    if (showToast) {
      if (response.status === 404) {
        toast.error('Resource not found', {
          description: errorMessage,
        });
      } else if (response.status === 400) {
        toast.error('Invalid request', {
          description: errorMessage,
        });
      } else if (response.status === 500) {
        toast.error('Server error', {
          description: 'Please try again later',
        });
      } else {
        toast.error('Request failed', {
          description: errorMessage,
        });
      }
    }
    
    throw new Error(errorMessage);
  }
  
  // Handle empty responses (like DELETE operations)
  if (response.status === 204 || response.status === 201 && response.headers.get('content-length') === '0') {
    return {} as T;
  }
  
  try {
    return await response.json();
  } catch {
    // If response is not JSON, return empty object
    return {} as T;
  }
}

/**
 * Helper function to create fetch requests with error handling
 */
async function fetchApi<T>(
  url: string,
  options: RequestInit = {},
  showToast = true
): Promise<T> {
  try {
    const response = await fetch(`${API_BASE_URL}${url}`, {
      ...options,
      headers: {
        'Content-Type': 'application/json',
        ...options.headers,
      },
    });
    
    return handleResponse<T>(response, showToast);
  } catch (error) {
    if (error instanceof Error) {
      // Network errors
      if (showToast) {
        toast.error('Network error', {
          description: 'Unable to connect to the server. Please check your connection.',
        });
      }
      throw error;
    }
    throw error;
  }
}

// User API
export const api = {
  users: {
    getAll: async (): Promise<User[]> => {
      return fetchApi<User[]>('/users');
    },
    
    getById: async (id: number): Promise<User> => {
      return fetchApi<User>(`/users/${id}`);
    },
    
    create: async (user: User): Promise<User> => {
      const result = await fetchApi<User>('/users', {
        method: 'POST',
        body: JSON.stringify(user),
      });
      toast.success('User created successfully!');
      return result;
    },
    
    update: async (id: number, user: User): Promise<User> => {
      const result = await fetchApi<User>(`/users/${id}`, {
        method: 'PUT',
        body: JSON.stringify(user),
      });
      toast.success('User updated successfully!');
      return result;
    },
    
    delete: async (id: number): Promise<void> => {
      await fetchApi<void>(`/users/${id}`, {
        method: 'DELETE',
      });
      toast.success('User deleted successfully!');
    },
    
    getBMR: async (id: number): Promise<number> => {
      return fetchApi<number>(`/users/${id}/bmr`, {}, false);
    },
    
    getTDEE: async (id: number): Promise<number> => {
      return fetchApi<number>(`/users/${id}/tdee`, {}, false);
    },
    
    getDashboardSummary: async (id: number): Promise<DashboardSummary> => {
      return fetchApi<DashboardSummary>(`/users/${id}/dashboard/summary`, {}, false);
    },
  },
  
  metrics: {
    getToday: async (userId: number): Promise<HealthMetrics | null> => {
      try {
        return await fetchApi<HealthMetrics>(`/users/${userId}/metrics/today`, {}, false);
      } catch {
        return null;
      }
    },
    
    getByDate: async (userId: number, date: string): Promise<HealthMetrics | null> => {
      try {
        return await fetchApi<HealthMetrics>(`/users/${userId}/metrics/date/${date}`, {}, false);
      } catch {
        return null;
      }
    },
    
    getRange: async (userId: number, startDate: string, endDate: string): Promise<HealthMetrics[]> => {
      return fetchApi<HealthMetrics[]>(
        `/users/${userId}/metrics/range?startDate=${startDate}&endDate=${endDate}`,
        {},
        false
      );
    },
    
    create: async (userId: number, metrics: HealthMetrics): Promise<HealthMetrics> => {
      const result = await fetchApi<HealthMetrics>(`/users/${userId}/metrics`, {
        method: 'POST',
        body: JSON.stringify(metrics),
      });
      toast.success('Health metrics saved successfully!');
      return result;
    },
    
    addSteps: async (userId: number, steps: number, date?: string): Promise<HealthMetrics> => {
      const url = date
        ? `/users/${userId}/metrics/steps?steps=${steps}&date=${date}`
        : `/users/${userId}/metrics/steps?steps=${steps}`;
      const result = await fetchApi<HealthMetrics>(url, {
        method: 'POST',
      });
      toast.success(`Added ${steps} steps!`);
      return result;
    },
  },
  
  activities: {
    getAll: async (userId: number): Promise<Activity[]> => {
      return fetchApi<Activity[]>(`/users/${userId}/activities`, {}, false);
    },
    
    getRange: async (userId: number, startDate: string, endDate: string): Promise<Activity[]> => {
      return fetchApi<Activity[]>(
        `/users/${userId}/activities/range?startDate=${startDate}&endDate=${endDate}`,
        {},
        false
      );
    },
    
    create: async (userId: number, activity: Activity): Promise<Activity> => {
      const result = await fetchApi<Activity>(`/users/${userId}/activities`, {
        method: 'POST',
        body: JSON.stringify(activity),
      });
      toast.success('Activity logged successfully!');
      return result;
    },
    
    update: async (userId: number, activityId: number, activity: Activity): Promise<Activity> => {
      const result = await fetchApi<Activity>(`/users/${userId}/activities/${activityId}`, {
        method: 'PUT',
        body: JSON.stringify(activity),
      });
      toast.success('Activity updated successfully!');
      return result;
    },
    
    delete: async (userId: number, activityId: number): Promise<void> => {
      await fetchApi<void>(`/users/${userId}/activities/${activityId}`, {
        method: 'DELETE',
      });
      toast.success('Activity deleted successfully!');
    },
  },
  
  dashboard: {
    getWeeklyStats: async (userId: number, weekStartDate?: string): Promise<WeeklyStats> => {
      const url = weekStartDate
        ? `/users/${userId}/dashboard/weekly?weekStartDate=${weekStartDate}`
        : `/users/${userId}/dashboard/weekly`;
      return fetchApi<WeeklyStats>(url, {}, false);
    },
  },
};
