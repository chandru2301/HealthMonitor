import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Plus, Edit, Trash2, Calendar as CalendarIcon } from "lucide-react";
import { Layout } from "@/components/Layout";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { api, Activity } from "@/lib/api";
import { useUser } from "@/contexts/UserContext";
import { toast } from "sonner";
import { format } from "date-fns";

const Activities = () => {
  const { currentUserId } = useUser();
  const navigate = useNavigate();
  const [activities, setActivities] = useState<Activity[]>([]);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [editingActivity, setEditingActivity] = useState<Activity | null>(null);
  const [formData, setFormData] = useState<Activity>({
    activityType: "",
    startTime: format(new Date(), "yyyy-MM-dd'T'HH:mm"),
    endTime: format(new Date(), "yyyy-MM-dd'T'HH:mm"),
    distanceKm: 0,
    notes: ""
  });

  useEffect(() => {
    if (!currentUserId) {
      navigate("/users");
      return;
    }
    loadActivities();
  }, [currentUserId, navigate]);

  const loadActivities = async () => {
    if (!currentUserId) return;
    
    try {
      const data = await api.activities.getAll(currentUserId);
      setActivities(data);
    } catch (error) {
      // Error toast is handled by API service
      console.error("Failed to load activities:", error);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!currentUserId) return;

    try {
      if (editingActivity?.id) {
        await api.activities.update(currentUserId, editingActivity.id, formData);
      } else {
        await api.activities.create(currentUserId, formData);
      }
      // Success toast is handled by API service
      setDialogOpen(false);
      setEditingActivity(null);
      resetForm();
      loadActivities();
    } catch (error) {
      // Error toast is handled by API service
    }
  };

  const handleDelete = async (activityId: number) => {
    if (!currentUserId) return;
    
    try {
      await api.activities.delete(currentUserId, activityId);
      // Success toast is handled by API service
      loadActivities();
    } catch (error) {
      // Error toast is handled by API service
    }
  };

  const handleEdit = (activity: Activity) => {
    setEditingActivity(activity);
    setFormData(activity);
    setDialogOpen(true);
  };

  const resetForm = () => {
    setFormData({
      activityType: "",
      startTime: format(new Date(), "yyyy-MM-dd'T'HH:mm"),
      endTime: format(new Date(), "yyyy-MM-dd'T'HH:mm"),
      distanceKm: 0,
      notes: ""
    });
  };

  const handleDialogClose = (open: boolean) => {
    setDialogOpen(open);
    if (!open) {
      setEditingActivity(null);
      resetForm();
    }
  };

  return (
    <Layout>
      <div className="space-y-6">
        <div className="flex items-center justify-between">
          <h1 className="text-3xl font-bold">Activities</h1>
          <Dialog open={dialogOpen} onOpenChange={handleDialogClose}>
            <DialogTrigger asChild>
              <Button>
                <Plus className="h-4 w-4 mr-2" />
                Add Activity
              </Button>
            </DialogTrigger>
            <DialogContent>
              <DialogHeader>
                <DialogTitle>{editingActivity ? "Edit Activity" : "Add New Activity"}</DialogTitle>
                <DialogDescription>Log your workout or exercise session</DialogDescription>
              </DialogHeader>
              <form onSubmit={handleSubmit} className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="activity-type">Activity Type *</Label>
                  <Input
                    id="activity-type"
                    required
                    placeholder="e.g., Running, Cycling, Yoga"
                    value={formData.activityType}
                    onChange={(e) => setFormData({ ...formData, activityType: e.target.value })}
                  />
                </div>
                
                <div className="grid gap-4 md:grid-cols-2">
                  <div className="space-y-2">
                    <Label htmlFor="start-time">Start Time *</Label>
                    <Input
                      id="start-time"
                      type="datetime-local"
                      required
                      value={formData.startTime}
                      onChange={(e) => setFormData({ ...formData, startTime: e.target.value })}
                    />
                  </div>
                  
                  <div className="space-y-2">
                    <Label htmlFor="end-time">End Time *</Label>
                    <Input
                      id="end-time"
                      type="datetime-local"
                      required
                      value={formData.endTime}
                      onChange={(e) => setFormData({ ...formData, endTime: e.target.value })}
                    />
                  </div>
                </div>
                
                <div className="space-y-2">
                  <Label htmlFor="distance">Distance (km)</Label>
                  <Input
                    id="distance"
                    type="number"
                    min="0"
                    step="0.01"
                    value={formData.distanceKm || ""}
                    onChange={(e) => setFormData({ ...formData, distanceKm: parseFloat(e.target.value) || 0 })}
                  />
                </div>
                
                <div className="space-y-2">
                  <Label htmlFor="notes">Notes</Label>
                  <Textarea
                    id="notes"
                    placeholder="Add any notes about this activity..."
                    value={formData.notes || ""}
                    onChange={(e) => setFormData({ ...formData, notes: e.target.value })}
                  />
                </div>
                
                <Button type="submit" className="w-full">
                  {editingActivity ? "Update Activity" : "Add Activity"}
                </Button>
              </form>
            </DialogContent>
          </Dialog>
        </div>

        <Card>
          <CardHeader>
            <CardTitle>Your Activities</CardTitle>
            <CardDescription>View and manage your logged activities</CardDescription>
          </CardHeader>
          <CardContent>
            {activities.length > 0 ? (
              <div className="rounded-md border">
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>Activity</TableHead>
                      <TableHead>Date & Time</TableHead>
                      <TableHead className="text-right">Duration</TableHead>
                      <TableHead className="text-right">Distance</TableHead>
                      <TableHead className="text-right">Calories</TableHead>
                      <TableHead className="text-right">Pace</TableHead>
                      <TableHead className="text-right">Actions</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {activities.map((activity) => (
                      <TableRow key={activity.id}>
                        <TableCell className="font-medium">{activity.activityType}</TableCell>
                        <TableCell>{format(new Date(activity.startTime), "MMM d, yyyy h:mm a")}</TableCell>
                        <TableCell className="text-right">{activity.durationMinutes?.toFixed(0)} min</TableCell>
                        <TableCell className="text-right">
                          {activity.distanceKm ? `${activity.distanceKm.toFixed(2)} km` : "-"}
                        </TableCell>
                        <TableCell className="text-right">{activity.caloriesBurned?.toFixed(0)} kcal</TableCell>
                        <TableCell className="text-right">
                          {activity.averagePace ? `${activity.averagePace.toFixed(1)} km/h` : "-"}
                        </TableCell>
                        <TableCell className="text-right">
                          <div className="flex justify-end gap-2">
                            <Button variant="ghost" size="sm" onClick={() => handleEdit(activity)}>
                              <Edit className="h-4 w-4" />
                            </Button>
                            <Button 
                              variant="ghost" 
                              size="sm" 
                              onClick={() => activity.id && handleDelete(activity.id)}
                            >
                              <Trash2 className="h-4 w-4" />
                            </Button>
                          </div>
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </div>
            ) : (
              <p className="text-center text-muted-foreground py-8">
                No activities logged yet. Add your first activity to get started!
              </p>
            )}
          </CardContent>
        </Card>
      </div>
    </Layout>
  );
};

export default Activities;
