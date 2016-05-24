#ifndef CV_DATASET_LOADER_HPP_
#define CV_DATASET_LOADER_HPP_

#include <string>

#include "opencv2/core/core.hpp"


class DatasetLoader {
public:
    virtual cv::Mat loadSamples(const std::string& filename) = 0;
    virtual cv::Mat loadResponses(const std::string& filename) = 0;
};

#endif  // define CV_DATASET_LOADER_HPP_