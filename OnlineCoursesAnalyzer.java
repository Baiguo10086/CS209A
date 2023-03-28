import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * This is just a demo for you, please run it on JDK17 (some statements may be not allowed in lower
 * version). This is just a demo, and you can extend and implement functions based on this demo, or
 * implement it in a different way.
 */
public class OnlineCoursesAnalyzer {

    List<Course> courses = new ArrayList<>();

    public OnlineCoursesAnalyzer(String datasetPath) {
        BufferedReader br = null;
        String line;
        try {
            br = new BufferedReader(new FileReader(datasetPath, StandardCharsets.UTF_8));
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] info = line.split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)", -1);
                Course course = new Course(info[0], info[1], new Date(info[2]), info[3], info[4],
                    info[5],
                    Integer.parseInt(info[6]), Integer.parseInt(info[7]), Integer.parseInt(info[8]),
                    Integer.parseInt(info[9]), Integer.parseInt(info[10]),
                    Double.parseDouble(info[11]),
                    Double.parseDouble(info[12]), Double.parseDouble(info[13]),
                    Double.parseDouble(info[14]),
                    Double.parseDouble(info[15]), Double.parseDouble(info[16]),
                    Double.parseDouble(info[17]),
                    Double.parseDouble(info[18]), Double.parseDouble(info[19]),
                    Double.parseDouble(info[20]),
                    Double.parseDouble(info[21]), Double.parseDouble(info[22]));
                courses.add(course);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //1
    public Map<String, Integer> getPtcpCountByInst() {

        Map<String, Integer> result = new HashMap<>();
        for (Course course : courses) {
            String institution = course.institution;
            int participants = course.participants;
            if (result.containsKey(institution)) {
                participants += result.get(institution);
            }
            result.put(institution, participants);
        }
        Map<String, Integer> sortedResult = new TreeMap<>(result);
        return sortedResult;
    }

    //2
    public Map<String, Integer> getPtcpCountByInstAndSubject() {

        Map<String, Integer> resultMap = new HashMap<>();

        for (Course course : courses) {
            String key = course.institution + "-" + course.subject;
            int count = course.participants;

            if (resultMap.containsKey(key)) {
                count += resultMap.get(key);
            }

            resultMap.put(key, count);
        }

        // Sorting the map by descending order of count
        Map<String, Integer> sortedMap = new LinkedHashMap<>();
        resultMap.entrySet()
            .stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()
                .thenComparing(Map.Entry.comparingByKey()))
            .forEachOrdered(entry -> sortedMap.put(entry.getKey(), entry.getValue()));

        return sortedMap;
    }

    //3
    public Map<String, List<List<String>>> getCourseListOfInstructor() {
        Map<String, List<List<String>>> courseListByInstructor = new HashMap<>();

        // Split the courses by instructor
        Map<String, List<Course>> coursesByInstructor = new HashMap<>();
        for (Course course : courses) {
            String[] instructors = course.instructors.split(",");
            for (String instructor : instructors) {
                instructor = instructor.trim();
                List<Course> coursesList = coursesByInstructor.getOrDefault(instructor,
                    new ArrayList<>());
                coursesList.add(course);
                coursesByInstructor.put(instructor, coursesList);
            }
        }

        // Sort the courses by title
        for (List<Course> courses : coursesByInstructor.values()) {
            courses.sort(Comparator.comparing(c -> c.title));
        }

        // Create the course list by instructor
        for (String instructor : coursesByInstructor.keySet()) {
            List<List<String>> courseList = new ArrayList<>();

            List<Course> independentCourses = new ArrayList<>();
            List<Course> coDevelopedCourses = new ArrayList<>();

            // Split the courses by independent and co-developed
            for (Course course : coursesByInstructor.get(instructor)) {
                if (course.instructors.split(",").length == 1) {
                    independentCourses.add(course);
                } else {
                    coDevelopedCourses.add(course);
                }
            }

            // Sort the courses by title
            independentCourses.sort(Comparator.comparing(c -> c.title));
            coDevelopedCourses.sort(Comparator.comparing(c -> c.title));

            // Add the course lists to the map
            List<String> independentCourseTitles = new ArrayList<>();
            for (Course course : independentCourses) {
                if (!independentCourseTitles.contains(course.title)) {
                    independentCourseTitles.add(course.title);
                }
            }
            courseList.add(independentCourseTitles);

            List<String> coDevelopedCourseTitles = new ArrayList<>();
            for (Course course : coDevelopedCourses) {
                if (!coDevelopedCourseTitles.contains(course.title)) {
                    coDevelopedCourseTitles.add(course.title);
                }
            }
            courseList.add(coDevelopedCourseTitles);

            courseListByInstructor.put(instructor, courseList);
        }

        return courseListByInstructor;
    }

    //4
    public List<String> getCourses(int topK, String by) {
        List<Course> sortedCourses = new ArrayList<>(courses);
        switch (by) {
            case "hours":
                sortedCourses.sort(Comparator.comparing(Course::getTotalHours).reversed()
                    .thenComparing(Course::getTitle));
                break;
            case "participants":
                sortedCourses.sort(Comparator.comparing(Course::getParticipants).reversed()
                    .thenComparing(Course::getTitle));
                break;
        }
        List<String> topKCourses = new ArrayList<>();
        int j = 0;
        for (int i = 0; i < topK; j++) {
            String title = sortedCourses.get(j).getTitle();
            if (!topKCourses.contains(title)) {
                i++;
                topKCourses.add(title);
            }
        }
        return topKCourses;
    }

    //5
    public List<String> searchCourses(String courseSubject, double percentAudited,
        double totalCourseHours) {
        // Create an empty list to hold the results
        List<String> results = new ArrayList<>();
        List<Course> All_Courses = new ArrayList<>(courses);

        // Convert the courseSubject to lowercase for case-insensitive matching
        courseSubject = courseSubject.toLowerCase();

        // Iterate over all courses to find matches
        for (Course course : All_Courses) {
            // Check if the course subject matches the given criteria
            if (course.getSubject().toLowerCase().contains(courseSubject)) {
                // Check if the percent audited and total course hours meet the given criteria
                if (course.getPercentAudited() >= percentAudited
                    && course.getTotalHours() <= totalCourseHours) {
                    // Add the course title to the results if it meets all the criteria
                    String courseTitle = course.getTitle();
                    if (!results.contains(courseTitle)) {
                        results.add(courseTitle);
                    }
                }
            }
        }

        // Sort the results alphabetically
        Collections.sort(results);

        // Return the sorted list of course titles
        return results;
    }

    //6
    public List<String> recommendCourses(int age, int gender, int isBachelorOrHigher) {
        List<Course> All_courses = new ArrayList<>(courses); // assume this method retrieves the list of available courses

        Map<String, List<Course>> course_unique = new HashMap<>();
        for (Course course : All_courses) {
            String number = course.number.trim();
            List<Course> coursesList = course_unique.getOrDefault(number,
                new ArrayList<>());
            coursesList.add(course);
            course_unique.put(number, coursesList);
        }
        List<Course> courseByNumber = new ArrayList<>();
        for (List<Course> course : course_unique.values()) {
            course.sort(Comparator.comparing(c -> c.launchDate));
            if (course.size() >= 2) {
                double averageMedianAge = 0;
                double averagePercentMale = 0;
                double averagePercentDegree = 0;
                for (Course c : course) {
                    averagePercentDegree += c.getPercentDegree();
                    averageMedianAge += c.getMedianAge();
                    averagePercentMale += c.getPercentMale();
                }
                averagePercentDegree /= course.size();
                averageMedianAge /= course.size();
                averagePercentMale /= course.size();
                course.get(course.size() - 1).medianAge = averageMedianAge;
                course.get(course.size() - 1).percentDegree = averagePercentDegree;
                course.get(course.size() - 1).percentMale = averagePercentMale;
            }
            courseByNumber.add(course.get(course.size() - 1));
        }
        List<Course> tmp = new ArrayList<>();
        for (Course c : courseByNumber) {
            c.medianAge = Math.pow(age - c.medianAge, 2)
                + Math.pow(gender * 100.0 - c.percentMale, 2)
                + Math.pow(isBachelorOrHigher * 100.0 - c.percentDegree, 2);
            tmp.add(c);
        }
        courseByNumber.sort(Comparator.comparing(
            c -> (Math.pow(age - c.medianAge, 2)
                + Math.pow(gender * 100.0 - c.percentMale, 2)
                + Math.pow(isBachelorOrHigher * 100.0 - c.percentDegree, 2)
            )));
        List<Course> tmp2 = tmp.stream()
            .sorted(Comparator.comparing(Course::getMedianAge).thenComparing(Course::getTitle))
            .toList();
        List<String> res = new ArrayList<>();

        for (int i = 0; i < tmp2.size(); i++)
        {
            if (res.size()>=10) break;
            if (!res.contains(tmp2.get(i).title)){
                res.add(tmp2.get(i).title);
            }

        }
        System.out.println(res);
        return res;
    }

}

class Course {

    String institution;
    String number;
    Date launchDate;
    String title;
    String instructors;
    String subject;
    int year;
    int honorCode;
    int participants;
    int audited;
    int certified;
    double percentAudited;
    double percentCertified;
    double percentCertified50;
    double percentVideo;
    double percentForum;
    double gradeHigherZero;
    double totalHours;
    double medianHoursCertification;
    double medianAge;
    double percentMale;
    double percentFemale;
    double percentDegree;

    public Course(String institution, String number, Date launchDate,
        String title, String instructors, String subject,
        int year, int honorCode, int participants,
        int audited, int certified, double percentAudited,
        double percentCertified, double percentCertified50,
        double percentVideo, double percentForum, double gradeHigherZero,
        double totalHours, double medianHoursCertification,
        double medianAge, double percentMale, double percentFemale,
        double percentDegree) {
        this.institution = institution;
        this.number = number;
        this.launchDate = launchDate;
        if (title.startsWith("\"")) {
            title = title.substring(1);
        }
        if (title.endsWith("\"")) {
            title = title.substring(0, title.length() - 1);
        }
        this.title = title;
        if (instructors.startsWith("\"")) {
            instructors = instructors.substring(1);
        }
        if (instructors.endsWith("\"")) {
            instructors = instructors.substring(0, instructors.length() - 1);
        }
        this.instructors = instructors;
        if (subject.startsWith("\"")) {
            subject = subject.substring(1);
        }
        if (subject.endsWith("\"")) {
            subject = subject.substring(0, subject.length() - 1);
        }
        this.subject = subject;
        this.year = year;
        this.honorCode = honorCode;
        this.participants = participants;
        this.audited = audited;
        this.certified = certified;
        this.percentAudited = percentAudited;
        this.percentCertified = percentCertified;
        this.percentCertified50 = percentCertified50;
        this.percentVideo = percentVideo;
        this.percentForum = percentForum;
        this.gradeHigherZero = gradeHigherZero;
        this.totalHours = totalHours;
        this.medianHoursCertification = medianHoursCertification;
        this.medianAge = medianAge;
        this.percentMale = percentMale;
        this.percentFemale = percentFemale;
        this.percentDegree = percentDegree;
    }

    public String getTitle() {
        return title;
    }

    public int getParticipants() {
        return participants;
    }

    public double getTotalHours() {
        return totalHours;
    }

    public double getPercentAudited() {
        return percentAudited;
    }

    public String getSubject() {
        return subject;
    }

    public String getInstitution() {
        return institution;
    }

    public String getNumber() {
        return number;
    }

    public Date getLaunchDate() {
        return launchDate;
    }

    public String getInstructors() {
        return instructors;
    }

    public int getYear() {
        return year;
    }

    public int getHonorCode() {
        return honorCode;
    }

    public int getAudited() {
        return audited;
    }

    public int getCertified() {
        return certified;
    }

    public double getPercentCertified() {
        return percentCertified;
    }

    public double getPercentCertified50() {
        return percentCertified50;
    }

    public double getPercentVideo() {
        return percentVideo;
    }

    public double getPercentForum() {
        return percentForum;
    }

    public double getGradeHigherZero() {
        return gradeHigherZero;
    }

    public double getMedianHoursCertification() {
        return medianHoursCertification;
    }

    public double getMedianAge() {
        return medianAge;
    }

    public double getPercentMale() {
        return percentMale;
    }

    public double getPercentFemale() {
        return percentFemale;
    }

    public double getPercentDegree() {
        return percentDegree;
    }
}